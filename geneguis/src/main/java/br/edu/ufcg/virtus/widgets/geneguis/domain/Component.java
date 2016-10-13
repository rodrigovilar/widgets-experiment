package br.edu.ufcg.virtus.widgets.geneguis.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Component {

	@Id
	@GeneratedValue
	private Long id;

	private String item;
	private String name;
	
	public Component() {
	}

	public Component(String item, String name) {
		this.item = item;
		this.name = name;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
