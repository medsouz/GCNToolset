package net.medsouz.gcn.gcmv;

import java.util.ArrayList;

import net.medsouz.gcn.model.bmd.BMD;
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
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
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
		cam.position.set(40f, 40f, 40f);
		cam.lookAt(0f, 0f, 0f);
		cam.near = 0.1f;
		cam.far = 300.0f;
		mb = new ModelBatch();
		ModelBuilder modelBuilder = new ModelBuilder();
		VTX1 vtx1 = (VTX1) model.getSection("VTX1");
		SHP1 shp1 = (SHP1) model.getSection("SHP1");
		ArrayList<Vector3> positions = getPositions(vtx1.getVertexFormat(VertexType.Position).data);
		
		//TODO: Add support for matrices to make it not a random blob of vertices
		for(Batch batch : shp1.batches) {
			modelBuilder.begin();
			MeshPartBuilder meshBuilder = modelBuilder.part("test", GL20.GL_LINES /* TODO: Change this based on the primitive's type */, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(Color.BLUE)));
			for(Vector3 pos : positions) {
				meshBuilder.vertex(pos, null, Color.GREEN, null);
			}
			for(Packet packet : batch.packets) {
				for(Primitive prim : packet.primitives) {
					for(Short index : prim.positionIndices) {
						meshBuilder.index(index);
					}
				}
			}
			Model mdl = modelBuilder.end();
			modelInstances.add(new ModelInstance(mdl));
		}
		
		//Model mdl = modelBuilder.createBox(10, 10, 10,  new Material(ColorAttribute.createDiffuse(Color.BLUE)), Usage.Position | Usage.Normal);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.8f, 0.8f, 1.0f));
	}

	@Override
	public void resize(int width, int height) {
		
	}

	@Override
	public void render() {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		cam.rotateAround(Vector3.Zero, new Vector3(0,1,0), 1f);
		cam.update();
		
		mb.begin(cam);
			for(ModelInstance modelInstance : modelInstances)
				mb.render(modelInstance);
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
		System.out.println("Positions: " + data.size());
		for(int x = 0; x < data.size(); x+=3) {
			Vector3 v3 = new Vector3((float)data.get(x), (float)data.get(x + 1), (float)data.get(x + 2));
			positions.add(v3);
		}
		System.out.println("Vertices: " + positions.size());
		return positions;
	}
	
}
