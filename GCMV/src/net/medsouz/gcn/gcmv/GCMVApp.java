package net.medsouz.gcn.gcmv;

import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.BMD;
import net.medsouz.gcn.model.bmd.sections.drw1.DRW1;
import net.medsouz.gcn.model.bmd.sections.drw1.DrawData;
import net.medsouz.gcn.model.bmd.sections.inf1.Hierarchy;
import net.medsouz.gcn.model.bmd.sections.inf1.HierarchyType;
import net.medsouz.gcn.model.bmd.sections.inf1.INF1;
import net.medsouz.gcn.model.bmd.sections.jnt1.JNT1;
import net.medsouz.gcn.model.bmd.sections.jnt1.JointEntry;
import net.medsouz.gcn.model.bmd.sections.shp1.Batch;
import net.medsouz.gcn.model.bmd.sections.shp1.Batch.Packet;
import net.medsouz.gcn.model.bmd.sections.shp1.Primitive;
import net.medsouz.gcn.model.bmd.sections.shp1.SHP1;
import net.medsouz.gcn.model.bmd.sections.vtx1.VTX1;
import net.medsouz.gcn.model.bmd.sections.vtx1.VertexType;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class GCMVApp implements ApplicationListener {

	BMD model;
	ArrayList<ModelInstance> modelInstances = new ArrayList<ModelInstance>();
	Camera cam;
	ModelBatch mb;
	Environment environment;
	
	public GCMVApp(BMD model) {
		this.model = model;
	}
	
	@Override
	public void create() {
		cam  = new PerspectiveCamera(75, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(80f, 80f, 80f);
		cam.lookAt(0f, 60f, 0f);
		cam.near = 0.1f;
		cam.far = 300.0f;
		mb = new ModelBatch();
		ModelBuilder modelBuilder = new ModelBuilder();
		INF1 inf1 = (INF1) model.getSection("INF1");
		VTX1 vtx1 = (VTX1) model.getSection("VTX1");
		SHP1 shp1 = (SHP1) model.getSection("SHP1");
		JNT1 jnt1 = (JNT1) model.getSection("JNT1");
		DRW1 drw1 = (DRW1) model.getSection("DRW1");
		ArrayList<Vector3> positions = getPositions(vtx1.getVertexFormat(VertexType.Position).data);
		ArrayList<Vector3> normals = getPositions(vtx1.getVertexFormat(VertexType.Normal).data);
		ArrayList<Matrix4> jointMatrices = getJointMatrices(jnt1.joints, inf1);
		ArrayList<Matrix4> lastMatrixTable = null;

		for(Hierarchy h : inf1.hierarchy) {
			if(h.type == HierarchyType.Shape) {
				Batch batch = shp1.batches.get(h.index);
				modelBuilder.begin();
				for(Packet packet : batch.packets) {
					ArrayList<Matrix4> matrixTable = new ArrayList<Matrix4>();
					for(int m = 0; m < packet.matrices.size(); m++) {
						Short matrixIndex = packet.matrices.get(m);
						if(matrixIndex == -1) {
							matrixTable.add(lastMatrixTable.get(m));
						} else {
							DrawData data = drw1.drawData.get(matrixIndex);
							if(data.weighted) {
								System.out.println("Weighted");
								matrixTable.add(new Matrix4());
							} else {
								matrixTable.add(jointMatrices.get(data.index));
							}
						}
					}
					lastMatrixTable = matrixTable;

					for(Primitive prim : packet.primitives) {
						//TODO: Change based on the primitive's type
						MeshPartBuilder meshBuilder = modelBuilder.part(prim.toString(), GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.GREEN)));
						for(int i = 0; i < prim.numVertices; i++) {
							if(batch.hasAttrib(VertexType.PositionMatrixIndex)) {
								Matrix4 mtx = matrixTable.get(prim.positionMatrixIndices.get(i));
								meshBuilder.setVertexTransform(mtx);
							} else {
								meshBuilder.setVertexTransform(matrixTable.get(0));
							}
							Vector3 pos = positions.get(prim.positionIndices.get(i));
							Vector3 nor = null;
							if(batch.hasAttrib(VertexType.Normal))
								nor = normals.get(prim.normalIndices.get(i));

							meshBuilder.index(meshBuilder.vertex(pos, nor, null, null));
						}
					}
				}
				Model mdl = modelBuilder.end();
				System.out.println("Model Parts: " + mdl.meshParts.size);
				modelInstances.add(new ModelInstance(mdl));
			}
		}
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
		environment.add(new DirectionalLight().set(1, 1, 1, 1, 0, 1));
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		cam.rotateAround(Vector3.Zero, new Vector3(0,1,0), 1f);
		cam.update();
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		mb.begin(cam);
			for(ModelInstance modelInstance : modelInstances)
				mb.render(modelInstance, environment);
		mb.end();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void dispose() {
		
	}

	
	public ArrayList<Vector3> getPositions(ArrayList<Object> data) {
		ArrayList<Vector3> positions = new ArrayList<Vector3>();
		for(int x = 0; x < data.size(); x+=3) {
			Vector3 v3 = new Vector3((float)data.get(x), (float)data.get(x + 1), (float)data.get(x + 2));
			positions.add(v3);
		}
		return positions;
	}
	
	public ArrayList<Matrix4> getJointMatrices(ArrayList<JointEntry> joints, INF1 inf1) {
		ArrayList<Matrix4> matrices = new ArrayList<Matrix4>();
		
		for(int j = 0; j < joints.size(); j++) {
			JointEntry jnt = joints.get(j);
			
			Vector3 position = new Vector3(jnt.posX, jnt.posY, jnt.posZ);
			Quaternion rotation = new Quaternion();
			rotation.setFromAxis(Vector3.X, jnt.rotX);
			rotation.setFromAxis(Vector3.Y, jnt.rotY);
			rotation.setFromAxis(Vector3.Z, jnt.rotZ);
			Vector3 scale = new Vector3(jnt.scaleX, jnt.scaleY, jnt.scaleZ);

			Matrix4 mat = new Matrix4(position, rotation, scale);
			//Apply hierarchy
			for(Hierarchy h : inf1.hierarchy) {
				if(h.type == HierarchyType.Joint && h.index == j) {
					Hierarchy par = h;
					do {
						if(par.parent == -1) {
							par = null;
							break;
						}
						par = inf1.hierarchy.get(par.parent);
					} while(par.type != HierarchyType.Joint);
					
					if(par != null) {
						Matrix4 p = matrices.get(par.index);
						mat.mulLeft(p);
					}
					break;
				}
			}
			matrices.add(mat);
		}
		
		return matrices;
	}
	
}
