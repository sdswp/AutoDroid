package com.gk.touchstone.core;

import com.gk.touchstone.entity.Plan;

public interface PlanRunner {
	//long duration = (long) plan.getDuration() * 60 * 60 * 1000;
	void start(Plan plan);
	void stop();
}
