package com.gk.touchstone.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBhelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "touchstone.db";
	private static final int DATABASE_VERSION = 2;

	public DBhelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	

	@Override
	public void onCreate(SQLiteDatabase db) {
		
		// 计划：一个计划包含多个任务
		db.execSQL("CREATE TABLE IF NOT EXISTS Plan(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"planName VARCHAR NOT NULL," +
				"planType INTEGER NOT NULL," +
				"createWay INTEGER NOT NULL," +
				"planNumber INTEGER NOT NULL," +
				"exceptions VARCHAR," +
				"isFinish INTEGER NOT NULL," +
				"duration FLOAT NOT NULL," +
				"createTime DATETIME," +
				"updateTime DATETIME)");
		
		// module	
		db.execSQL("CREATE TABLE IF NOT EXISTS Module(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"name VARCHAR NOT NULL ," +
				"displayName VARCHAR NOT NULL," +
				"desc VARCHAR," +
				"upateTime DATETIME," +
				"createTime DATETIME)");

		// 任务		
		db.execSQL("CREATE TABLE IF NOT EXISTS Task(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"planId INTEGER NOT NULL," +
				"taskName VARCHAR NOT NULL ," +
				"displayName VARCHAR," +
				"taskAction VARCHAR NOT NULL," +
				"formValue VARCHAR," +
				"originCount INTEGER NOT NULL," +
				"taskCount INTEGER NOT NULL," +
				"resultJson TEXT," +
				"resultFile VARCHAR NOT NULL," +
				//"isEnable INTEGER NOT NULL," +
				"createTime DATETIME," +
				"updateTime DATETIME)");
				//"FOREIGN KEY(plan_id) REFERENCES Plan(id))");
		
		// testcase	
		db.execSQL("CREATE TABLE IF NOT EXISTS TestCase(" +
				"id INTEGER PRIMARY KEY AUTOINCREMENT," +
				"serverId VARCHAR NOT NULL," +
				"moduleId INTEGER NOT NULL," +
				"name VARCHAR NOT NULL ," +
				"displayName VARCHAR NOT NULL," +
				"value VARCHAR," +
				"action VARCHAR," +
				"entity VARCHAR," +
				"aloneUse VARCHAR," +
				"isJoin INTEGER NOT NULL," +
				"count INTEGER NOT NULL," +
				"desc VARCHAR," +
				"createTime DATETIME," +
				"updateTime DATETIME)");
		
		// 结果
		db.execSQL("CREATE TABLE IF NOT EXISTS Result(" +
				"taskId INTEGER NOT NULL," +
				"testcaseId INTEGER NOT NULL," +
				"deviceId VARCHAR," +
				"result TEXT," +
				"resultFile VARCHAR NOT NULL," +
				"desc VARCHAR," +
				"startTime DATETIME," +
				"endTime DATETIME)");
				//"FOREIGN KEY(task_id) REFERENCES Task(id)," +
				//"FOREIGN KEY(task_name) REFERENCES Task(taskName))");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion == newVersion) {
			return;
		}

		String[] tables = new String[] { "Plan", "Module", "Task", "TestCase",
				"Result" };
		for (String str : tables) {
			try {
				db.execSQL("DROP TABLE IF EXISTS " + str);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		onCreate(db);
	}

}
