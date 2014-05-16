package com.cowthegreat.shmup.controllers;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Pool;

public class EnemyControllerFactory {
	public static Skin s = null;
	
	// ==================================================
	// -- IBC -------------------------------------------
	// ==================================================
	
	private static final Pool<IdiotBroController> ibcPool = new Pool<IdiotBroController>(4, 15){
		protected IdiotBroController newObject() {
			System.out.println("new ibc");
			return new IdiotBroController(s);
		}
	};
	
	public static IdiotBroController acquireIdiot(){
		if(s == null) return null;
		IdiotBroController ibc = ibcPool.obtain();
		ibc.initialize(s);
		return ibc;
	}
	public static void freeIdiot(IdiotBroController ibc){
		ibcPool.free(ibc);
	}
	
	// =================================================
	// -- DBC ------------------------------------------
	// =================================================
	
	private static final Pool<DashBroController> dbcPool = new Pool<DashBroController>(2, 5){
		protected DashBroController newObject() {
			System.out.println("new dbc");
			return new DashBroController(s);
		}
	};
	
	public static DashBroController acquireDash(){
		if(s == null) return null;
		DashBroController dbc = dbcPool.obtain();
		dbc.initialize(s);
		return dbc;
	}
	
	public static void freeDash(DashBroController dbc){
		dbcPool.free(dbc);
	}
	
	// =================================================
	// -- SBC ------------------------------------------
	// =================================================
	
	private static final Pool<ShieldBroController> sbcPool = new Pool<ShieldBroController>(2, 5){
		protected ShieldBroController newObject() {
			System.out.println("new sbc");
			return new ShieldBroController(s);
		}
	};
	
	public static ShieldBroController acquireShield(){
		if(s == null) return null;
		ShieldBroController sbc = sbcPool.obtain();
		sbc.initialize(s);
		return sbc;
	}
	
	public static void freeShield(ShieldBroController sbc){
		sbcPool.free(sbc);
	}
	
	// =================================================
	// -- EBC ------------------------------------------
	// =================================================
	
	private static final Pool<SplodeBroController> ebcPool = new Pool<SplodeBroController>(1, 3){
		protected SplodeBroController newObject() {
			System.out.println("new ebc");
			return new SplodeBroController(s);
		}
	};
	
	public static SplodeBroController acquireSplode(){
		if(s == null) return null;
		SplodeBroController ebc = ebcPool.obtain();
		ebc.initialize(s);
		return ebc;
	}
	
	public static void freeSplode(SplodeBroController ebc){
		ebcPool.free(ebc);
	}
	
	public static void free(EnemyController ec){
		if(ec instanceof IdiotBroController) {
			freeIdiot((IdiotBroController)ec);
		} else if(ec instanceof DashBroController){
			freeDash((DashBroController)ec);
		} else if (ec instanceof ShieldBroController) {
			freeShield((ShieldBroController)ec);
		} else if (ec instanceof SplodeBroController) {
			freeSplode((SplodeBroController)ec);
		}
	}
}
