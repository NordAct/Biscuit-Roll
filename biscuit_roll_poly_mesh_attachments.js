/// <reference types="blockbench-types" />

/**
 * Vertex Locator Binder
 * - Bind mesh vertices to locators (Bedrock Entity)
 * - Save bindings in .bbmodel
 * - Export JSON: bone → locator → [x, y, z]  (X inverted on export)
 * - Preview: bound vertices follow locators in Animate AND Paint modes
 */

/**
 * Author's note: this plugin is entirely vibecoded. Only thing I've done is ensured that it works and doesn't break the second you do something
 *
 * Use instructions:
 *  - Create mesh in your Bedrock Entity project (creating meshes may require Meshy plugin, just look it up in app)
 *  - Create locators to which you plan attach mesh's vertices
 *  - Select vertex you want attach to specific locator
 *  - Open Tools->Bind Vertices to Locator (shows up only when you have at least one vertex selected) and select locator you need from the list
 *  - Alternatively to manually making all of the locators and attaching them by hand, you can open context menu for your mesh and select "Generate Locators for Vertices". This will generate and automatically bind locators for mesh (or selected vertices) and place them in same group as mesh
 *  - To unbind vertex from locator, select vertices you want to unbind and go to Tools->Unind Selected Vertices
 *
 * This plugin as well supports accurate preview of how mesh is going to look in game with all of this attachments. There's a few specifics on how mesh must be set up in order to work correctly:
 *  - Since Bedrock format supports only single mesh per bone, you must know that if you add several meshes to the bone all of them are going to be merged into single one after export
 *  - Bone, which contains mesh, must not be animated. Even if blockbench displays everything like everything is fine, in game mesh will not display correctly due transforms from the bone being applied on the mesh
 *  - To help with previous point, create group/bone at root and place your mesh here and do not touch it during animation (or even better, just lock the bone)
 */

(function () {
    'use strict';

    const PLUGIN_ID = 'biscuit_roll_poly_mesh_attachments';
    const BINDINGS_KEY = 'biscuit_roll_poly_mesh_attachments';
    const REST_KEY = '_vlb_rest_vertices';

    let bindAction, unbindAction, exportAction, clearAction, followToggle, generateLocatorsAction;
    let originalGetSaveCopy, originalExtend, originalAnimatorPreview;
    let followEnabled = true;
    let previewActive = false;
    let paintPreviewTimer = null;

    // ─── Helpers ───────────────────────────────────────────────

    function ensureBindings(mesh) {
        if (!mesh[BINDINGS_KEY] || typeof mesh[BINDINGS_KEY] !== 'object') {
            mesh[BINDINGS_KEY] = {};
        }
        return mesh[BINDINGS_KEY];
    }

    function getBoneName(mesh) {
        let parent = mesh.parent;
        while (parent && parent !== 'root') {
            if (parent instanceof Group) return parent.name || 'unnamed';
            parent = parent.parent;
        }
        return 'root';
    }

    function getLocatorByUUID(uuid) {
        return Locator.all.find(l => l.uuid === uuid) || null;
    }

    function isBedrockFormat() {
        return Format && (Format.id === 'bedrock' || Format.id === 'bedrock_old');
    }

    function hasSelectedVertices() {
        return Mesh.selected.some(m => m.getSelectedVertices().length > 0);
    }

    function meshesWithBindings() {
        return Mesh.all.filter(m => {
            const b = m[BINDINGS_KEY];
            return b && typeof b === 'object' && Object.keys(b).length > 0;
        });
    }

    function isPreviewMode() {
        if (typeof Mode === 'undefined' || !Mode.selected) return false;
        const id = Mode.selected.id;
        return id === 'animate' || id === 'paint';
    }

    function sanitizeNamePart(name) {
        return String(name || 'mesh').replace(/\s+/g, '_');
    }

    /** Vertex keys: selected if any, otherwise all */
    function getVertexKeysForGenerate(mesh) {
        const selected = mesh.getSelectedVertices();
        if (selected && selected.length) return selected.slice();
        return Object.keys(mesh.vertices || {});
    }

    function getParentGroupForMesh(mesh) {
        const parent = mesh.parent;
        if (parent && parent !== 'root' && parent instanceof Group) return parent;
        return undefined; // root
    }

    // ─── Rest pose ─────────────────────────────────────────────

    function captureRestPose(mesh) {
        if (!mesh.vertices) return;
        const copy = {};
        for (const key in mesh.vertices) {
            const v = mesh.vertices[key];
            if (v) copy[key] = [v[0], v[1], v[2]];
        }
        mesh[REST_KEY] = copy;
    }

    function ensureRestPose(mesh) {
        if (!mesh[REST_KEY] || typeof mesh[REST_KEY] !== 'object') {
            captureRestPose(mesh);
        }
        return mesh[REST_KEY];
    }

    function restoreRestPose(mesh) {
        const rest = mesh[REST_KEY];
        if (!rest || !mesh.vertices) return false;
        let changed = false;
        for (const key in rest) {
            const r = rest[key];
            const v = mesh.vertices[key];
            if (!r || !v) continue;
            if (v[0] !== r[0] || v[1] !== r[1] || v[2] !== r[2]) {
                v[0] = r[0];
                v[1] = r[1];
                v[2] = r[2];
                changed = true;
            }
        }
        return changed;
    }

    function restoreAllRestPoses() {
        const meshes = meshesWithBindings();
        const updated = [];
        meshes.forEach(mesh => {
            if (restoreRestPose(mesh)) updated.push(mesh);
        });
            if (updated.length) {
                Canvas.updateView({
                    elements: updated,
                    element_aspects: { geometry: true }
                });
            }
            previewActive = false;
    }

    // ─── Follow locators ───────────────────────────────────────

    function applyBindingsForPreview() {
        if (!followEnabled) return;
        if (!isBedrockFormat()) return;
        if (!isPreviewMode()) {
            if (previewActive) restoreAllRestPoses();
            return;
        }

        const meshes = meshesWithBindings();
        if (!meshes.length) return;

        const updated = [];
        const world = new THREE.Vector3();
        const invMesh = new THREE.Matrix4();

        meshes.forEach(mesh => {
            const bindings = mesh[BINDINGS_KEY];
            if (!bindings) return;

            ensureRestPose(mesh);

            const meshObj = mesh.mesh || mesh.scene_object;
            if (!meshObj) return;

            meshObj.updateMatrixWorld(true);
            invMesh.copy(meshObj.matrixWorld).invert();

            let changed = false;

            for (const vkey of Object.keys(bindings)) {
                const locatorUUID = bindings[vkey];
                const locator = getLocatorByUUID(locatorUUID);
                if (!locator) continue;

                const locObj = locator.mesh || locator.scene_object;
                if (!locObj) continue;

                locObj.updateMatrixWorld(true);
                world.setFromMatrixPosition(locObj.matrixWorld);
                world.applyMatrix4(invMesh);

                const v = mesh.vertices[vkey];
                if (!v) continue;

                if (v[0] !== world.x || v[1] !== world.y || v[2] !== world.z) {
                    v[0] = world.x;
                    v[1] = world.y;
                    v[2] = world.z;
                    changed = true;
                }
            }

            if (changed) updated.push(mesh);
        });

            if (updated.length) {
                previewActive = true;
                Canvas.updateView({
                    elements: updated,
                    element_aspects: { geometry: true }
                });
            }
    }

    function installAnimatorHook() {
        if (typeof Animator === 'undefined' || !Animator.preview) return;

        originalAnimatorPreview = Animator.preview.bind(Animator);
        Animator.preview = function (in_loop) {
            const result = originalAnimatorPreview(in_loop);
            try {
                applyBindingsForPreview();
            } catch (e) {
                console.warn('[Vertex Locator Binder] preview error:', e);
            }
            return result;
        };
    }

    function uninstallAnimatorHook() {
        if (originalAnimatorPreview && typeof Animator !== 'undefined') {
            Animator.preview = originalAnimatorPreview;
            originalAnimatorPreview = null;
        }
        stopPaintPreviewLoop();
        restoreAllRestPoses();
    }

    function startPaintPreviewLoop() {
        stopPaintPreviewLoop();
        paintPreviewTimer = setInterval(() => {
            if (!followEnabled || !isPreviewMode()) {
                stopPaintPreviewLoop();
                return;
            }
            if (typeof Mode !== 'undefined' && Mode.selected && Mode.selected.id === 'paint') {
                try {
                    applyBindingsForPreview();
                } catch (e) { /* ignore */ }
            }
        }, 50);
    }

    function stopPaintPreviewLoop() {
        if (paintPreviewTimer) {
            clearInterval(paintPreviewTimer);
            paintPreviewTimer = null;
        }
    }

    function onModeChange() {
        if (!isPreviewMode()) {
            stopPaintPreviewLoop();
            restoreAllRestPoses();
            return;
        }

        if (Mode.selected.id === 'paint') {
            startPaintPreviewLoop();
        } else {
            stopPaintPreviewLoop();
        }

        if (!followEnabled) return;

        try {
            if (typeof Animator !== 'undefined' && Animator.open) {
                Animator.preview();
            } else {
                applyBindingsForPreview();
            }
        } catch (e) {
            applyBindingsForPreview();
        }
    }

    // ─── Persistence ───────────────────────────────────────────

    function installPersistence() {
        originalGetSaveCopy = Mesh.prototype.getSaveCopy;
        Mesh.prototype.getSaveCopy = function (...args) {
            const hadPreview = previewActive && this[REST_KEY];
            if (hadPreview) restoreRestPose(this);

            const copy = originalGetSaveCopy.apply(this, args);
            const bindings = this[BINDINGS_KEY];
            if (bindings && typeof bindings === 'object' && Object.keys(bindings).length) {
                copy[BINDINGS_KEY] = JSON.parse(JSON.stringify(bindings));
            }
            return copy;
        };

        originalExtend = Mesh.prototype.extend;
        Mesh.prototype.extend = function (data) {
            originalExtend.call(this, data);
            if (data && data[BINDINGS_KEY] && typeof data[BINDINGS_KEY] === 'object') {
                this[BINDINGS_KEY] = JSON.parse(JSON.stringify(data[BINDINGS_KEY]));
            } else if (!this[BINDINGS_KEY]) {
                this[BINDINGS_KEY] = {};
            }
            delete this[REST_KEY];
            return this;
        };
    }

    function uninstallPersistence() {
        if (originalGetSaveCopy) {
            Mesh.prototype.getSaveCopy = originalGetSaveCopy;
            originalGetSaveCopy = null;
        }
        if (originalExtend) {
            Mesh.prototype.extend = originalExtend;
            originalExtend = null;
        }
    }

    // ─── Export (X inverted) ───────────────────────────────────

    function buildExportData() {
        const result = {};

        Mesh.all.forEach(mesh => {
            const bindings = mesh[BINDINGS_KEY];
            if (!bindings || !Object.keys(bindings).length) return;

            const verts = mesh[REST_KEY] || mesh.vertices;
            const boneName = getBoneName(mesh);
            if (!result[boneName]) result[boneName] = {};

            for (const [vkey, locatorUUID] of Object.entries(bindings)) {
                const locator = getLocatorByUUID(locatorUUID);
                if (!locator) continue;

                const local = verts[vkey];
                if (!local) continue;
                const o = mesh.origin || [0, 0, 0];
                const pos = [
                    -(o[0] + local[0]),
                         o[1] + local[1],
                         o[2] + local[2]
                ];
                result[boneName][locator.name] = pos;
            }
        });

        return result;
    }

    // ─── Generate locators from vertices ───────────────────────

    function makeLocatorName(meshName, index, side) {
        const base = sanitizeNamePart(meshName);
        const n = index; // 1-based
        if (side === 'right') {
            return base + '_locator' + n;
        }
        return 'locator' + n + '_' + base;
    }

    function generateLocatorsForMeshes(meshes, nameSide) {
        const created = [];
        let totalVerts = 0;

        meshes.forEach(mesh => {
            const vkeys = getVertexKeysForGenerate(mesh);
            if (!vkeys.length) return;

            if (previewActive) restoreRestPose(mesh);
            captureRestPose(mesh);

            const origin = mesh.origin || [0, 0, 0];
            const parent = getParentGroupForMesh(mesh);
            const bindings = ensureBindings(mesh);
            const meshName = mesh.name || 'mesh';

            vkeys.forEach((vkey, i) => {
                const local = (mesh[REST_KEY] && mesh[REST_KEY][vkey]) || mesh.vertices[vkey];
                if (!local) return;

                const pos = [
                    origin[0] + local[0],
                    origin[1] + local[1],
                    origin[2] + local[2]
                ];

                const name = makeLocatorName(meshName, i + 1, nameSide);

                const locator = new Locator({
                    name: name,
                    position: pos.slice(),
                                            from: pos.slice(), // compatibility with older BB if needed
                                            visibility: true,
                                            export: true
                });

                locator.init();
                if (parent) {
                    locator.addTo(parent);
                } else {
                    locator.addTo();
                }

                bindings[vkey] = locator.uuid;
                created.push(locator);
                totalVerts++;
            });
        });

        return { created, totalVerts };
    }

    function openGenerateLocatorsDialog(meshes) {
        if (!meshes || !meshes.length) {
            Blockbench.showQuickMessage('Select a mesh first');
            return;
        }

        let totalKeys = 0;
        meshes.forEach(m => {
            totalKeys += getVertexKeysForGenerate(m).length;
        });
        if (!totalKeys) {
            Blockbench.showQuickMessage('No vertices to generate locators from');
            return;
        }

        new Dialog({
            id: 'vlb_generate_locators_dialog',
            title: 'Generate Locators for Vertices',
            form: {
                info: {
                    type: 'info',
                    text: `Will create ${totalKeys} locator(s) from selected/all vertices.`
                },
                name_side: {
                    label: 'Locator name placement',
                    type: 'select',
                    value: 'left',
                    options: {
                        left: 'Left — locatorN_meshName',
                        right: 'Right — meshName_locatorN'
                    }
                }
            },
            buttons: ['Generate', 'Cancel'],
            onConfirm(formResult) {
                const nameSide = (formResult && formResult.name_side) || 'left';

                Undo.initEdit({
                    elements: [...meshes, ...Locator.all],
                    outliner: true,
                    selection: true
                });

                const { created, totalVerts } = generateLocatorsForMeshes(meshes, nameSide);

                Undo.finishEdit('Generate locators for vertices');

                if (created.length) {
                    Canvas.updateView({
                        elements: [...meshes, ...created],
                        element_aspects: { transform: true, geometry: true },
                        selection: true
                    });
                }

                Blockbench.showQuickMessage(
                    created.length
                    ? `Generated ${created.length} locator(s) and bound to vertices`
                    : 'No locators generated'
                );

                if (typeof Animator !== 'undefined' && Animator.open) {
                    Animator.preview();
                } else if (isPreviewMode()) {
                    applyBindingsForPreview();
                }
            }
        }).show();
    }

    // ─── Actions ───────────────────────────────────────────────

    function createActions() {
        bindAction = new Action('vlb_bind_vertices', {
            name: 'Bind Vertices to Locator',
            description: 'Bind selected mesh vertices to a locator',
            icon: 'link',
            category: 'edit',
            condition: () => isBedrockFormat() && hasSelectedVertices(),
                                click() {
                                    const meshes = Mesh.selected.filter(m => m.getSelectedVertices().length > 0);
                                    if (!meshes.length) {
                                        Blockbench.showQuickMessage('Select mesh vertices first');
                                        return;
                                    }

                                    const locators = Locator.all;
                                    if (!locators.length) {
                                        Blockbench.showQuickMessage('No locators in the model');
                                        return;
                                    }

                                    const options = {};
                                    locators.forEach(l => {
                                        options[l.uuid] = l.name || l.uuid;
                                    });

                                    new Dialog({
                                        id: 'vlb_bind_dialog',
                                        title: 'Bind Vertices to Locator',
                                        form: {
                                            locator: {
                                                label: 'Locator',
                                                type: 'select',
                                                options
                                            }
                                        },
                                        onConfirm(formData) {
                                            const locatorUUID = formData.locator;
                                            if (!locatorUUID) return;

                                            Undo.initEdit({ elements: meshes });

                                            let count = 0;
                                            meshes.forEach(mesh => {
                                                if (previewActive) restoreRestPose(mesh);
                                                captureRestPose(mesh);

                                                const bindings = ensureBindings(mesh);
                                                mesh.getSelectedVertices().forEach(vkey => {
                                                    bindings[vkey] = locatorUUID;
                                                    count++;
                                                });
                                            });

                                            Undo.finishEdit('Bind vertices to locator');
                                            Blockbench.showQuickMessage(`Bound ${count} vertex(es)`);

                                            if (typeof Animator !== 'undefined' && Animator.open) {
                                                Animator.preview();
                                            } else if (isPreviewMode()) {
                                                applyBindingsForPreview();
                                            }
                                        }
                                    }).show();
                                }
        });

        unbindAction = new Action('vlb_unbind_vertices', {
            name: 'Unbind Selected Vertices',
            description: 'Remove locator bindings from selected vertices',
            icon: 'link_off',
            category: 'edit',
            condition: () => isBedrockFormat() && hasSelectedVertices(),
                                  click() {
                                      const meshes = Mesh.selected.filter(m => m.getSelectedVertices().length > 0);
                                      if (!meshes.length) return;

                                      Undo.initEdit({ elements: meshes });

                                      let count = 0;
                                      meshes.forEach(mesh => {
                                          if (previewActive) restoreRestPose(mesh);
                                          const bindings = mesh[BINDINGS_KEY];
                                          if (!bindings) return;
                                          mesh.getSelectedVertices().forEach(vkey => {
                                              if (bindings[vkey]) {
                                                  delete bindings[vkey];
                                                  count++;
                                              }
                                          });
                                          captureRestPose(mesh);
                                      });

                                      Undo.finishEdit('Unbind vertices');
                                      Blockbench.showQuickMessage(count ? `Unbound ${count} vertex(es)` : 'No bindings on selection');

                                      if (typeof Animator !== 'undefined' && Animator.open) {
                                          Animator.preview();
                                      } else if (isPreviewMode()) {
                                          applyBindingsForPreview();
                                      }
                                  }
        });

        clearAction = new Action('vlb_clear_mesh_bindings', {
            name: 'Clear All Bindings on Selected Meshes',
            description: 'Remove all vertex–locator bindings from selected meshes',
            icon: 'delete_sweep',
            category: 'edit',
            condition: () => isBedrockFormat() && Mesh.selected.length > 0,
                                 click() {
                                     const meshes = Mesh.selected.slice();
                                     Undo.initEdit({ elements: meshes });
                                     meshes.forEach(mesh => {
                                         if (previewActive) restoreRestPose(mesh);
                                         mesh[BINDINGS_KEY] = {};
                                         delete mesh[REST_KEY];
                                     });
                                     Undo.finishEdit('Clear vertex locator bindings');
                                     Blockbench.showQuickMessage('Cleared bindings on selected meshes');
                                 }
        });

        generateLocatorsAction = new Action('vlb_generate_locators', {
            name: 'Generate Locators for Vertices',
            description: 'Create locators at mesh vertices, parent them to the mesh group, and bind them',
            icon: 'control_point',
            category: 'edit',
            condition: () => isBedrockFormat() && Mesh.selected.length > 0,
                                            click() {
                                                openGenerateLocatorsDialog(Mesh.selected.slice());
                                            }
        });

        exportAction = new Action('vlb_export_bindings', {
            name: 'Export Vertex–Locator Bindings',
            description: 'Export bound vertices as JSON (bone → locator → [x,y,z], X inverted)',
                                  icon: 'file_download',
                                  category: 'file',
                                  condition: () => isBedrockFormat(),
                                  click() {
                                      const data = buildExportData();
                                      if (!Object.keys(data).length) {
                                          Blockbench.showQuickMessage('No vertex–locator bindings to export');
                                          return;
                                      }

                                      Blockbench.export({
                                          resource_id: 'vertex_locator_bindings',
                                          type: 'Vertex Locator Bindings',
                                          extensions: ['json'],
                                          name: (Project.name || 'model') + '_vertex_bindings',
                                                        content: autoStringify(data)
                                      });
                                  }
        });

        followToggle = new Toggle('vlb_follow_locators', {
            name: 'Vertices Follow Locators',
            description: 'In Animate/Paint mode, bound mesh vertices follow their locators (preview only)',
                                  icon: 'animation',
                                  category: 'animation',
                                  default: true,
                                      condition: () => isBedrockFormat(),
                                  onChange(value) {
                                      followEnabled = !!value;
                                      if (!followEnabled) {
                                          stopPaintPreviewLoop();
                                          restoreAllRestPoses();
                                      } else if (isPreviewMode()) {
                                          if (Mode.selected.id === 'paint') startPaintPreviewLoop();
                                          if (typeof Animator !== 'undefined' && Animator.open) {
                                              Animator.preview();
                                          } else {
                                              applyBindingsForPreview();
                                          }
                                      }
                                  }
        });
    }

    function addToMenus() {
        MenuBar.menus.tools.addAction(bindAction);
        MenuBar.menus.tools.addAction(unbindAction);
        MenuBar.menus.tools.addAction(clearAction);
        MenuBar.menus.tools.addAction(generateLocatorsAction);
        MenuBar.menus.tools.addAction(followToggle);
        MenuBar.addAction(exportAction, 'file.export');

        // Context menu on mesh (outliner / element menu)
        try {
            if (Mesh.prototype.menu && Mesh.prototype.menu.addAction) {
                Mesh.prototype.menu.addAction(generateLocatorsAction);
            }
        } catch (e) {
            console.warn('[Vertex Locator Binder] Could not add to Mesh.prototype.menu', e);
        }

        // Fallback: outliner panel menu
        try {
            if (Interface && Interface.Panels && Interface.Panels.outliner && Interface.Panels.outliner.menu) {
                Interface.Panels.outliner.menu.addAction(generateLocatorsAction);
            }
        } catch (e) { /* ignore */ }
    }

    function removeFromMenus() {
        if (exportAction) {
            try {
                MenuBar.removeAction('file.export.vlb_export_bindings');
            } catch (e) { /* ignore */ }
            try {
                MenuBar.menus.file.removeAction('export.vlb_export_bindings');
            } catch (e) { /* ignore */ }
        }

        try {
            if (Mesh.prototype.menu && Mesh.prototype.menu.removeAction) {
                Mesh.prototype.menu.removeAction(generateLocatorsAction);
            }
        } catch (e) { /* ignore */ }

        try {
            if (Interface && Interface.Panels && Interface.Panels.outliner && Interface.Panels.outliner.menu) {
                Interface.Panels.outliner.menu.removeAction(generateLocatorsAction);
            }
        } catch (e) { /* ignore */ }

        [bindAction, unbindAction, clearAction, generateLocatorsAction, exportAction, followToggle].forEach(a => {
            if (a && a.delete) a.delete();
        });
            bindAction = unbindAction = clearAction = generateLocatorsAction = exportAction = followToggle = null;
    }

    // ─── Plugin registration ───────────────────────────────────

    Plugin.register(PLUGIN_ID, {
        title: 'Biscuit Roll Poly Mesh Attachments',
        author: 'Nord_Act',
        description:
        'Allows attaching vertices to locators and expirting attachments as poly mesh attachments for Biscuit Roll. Note: this is very much vibecoded plugin. Please do not throw your slippers at me',
        icon: 'link',
        version: '1.1.0',
        variant: 'both',
        tags: ['Minecraft: Java Edition'],
        min_version: '5.0.0',

        onload() {
            installPersistence();
            installAnimatorHook();
            createActions();
            addToMenus();

            if (typeof Blockbench !== 'undefined' && Blockbench.on) {
                Blockbench.on('select_mode', onModeChange);
            }

            // If already in Paint when plugin loads
            if (isPreviewMode() && Mode.selected.id === 'paint' && followEnabled) {
                startPaintPreviewLoop();
            }
        },

        onunload() {
            if (typeof Blockbench !== 'undefined' && Blockbench.removeListener) {
                Blockbench.removeListener('select_mode', onModeChange);
            }
            stopPaintPreviewLoop();
            uninstallAnimatorHook();
            removeFromMenus();
            uninstallPersistence();
        }
    });
})();
