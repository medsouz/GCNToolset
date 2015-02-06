package net.medsouz.gcn.gcmv;

import java.io.File;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.medsouz.gcn.model.bmd.BMD;

public class GCMV {
	public static void main(String[] args) {
		System.out.println("============================");
		System.out.println("GameCube Model Viewer (GCMV)");
		System.out.println("By Matt \"medsouz\" Souza");
		System.out.println("============================");
		
		BMD model = new BMD();
		model.read(new File(args[0]));
		
		if(args.length > 1) {
			//Draw window
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "GameCube Model Viewer (GCMV)";
			new LwjglApplication(new GCMVApp(model), config);
		}
	}
}
