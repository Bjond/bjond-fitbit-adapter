package com.bjond.adapter.definition;

import lombok.Data;

@Data
public class FitbitConsequence {
	private String id;
	private String jsonKey;
	private String name;
	private String description;
	private String webhook;
	private String serviceid;
}
