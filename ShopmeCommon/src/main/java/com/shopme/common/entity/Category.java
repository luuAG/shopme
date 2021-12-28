package com.shopme.common.entity;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.shopme.common.constant.Constants;

@Entity
@Table(name="categories")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(length = 128, nullable = false, unique = true)
	private String name;
	
	@Column(length = 64, nullable = false, unique = true)
	private String alias;
	
	@Column(length = 64, nullable = false, unique=false)
	private String image;
	
	private boolean enabled;
	
	@Column(name = "all_parent_ids", length = 255, nullable = true)
	private String allParentIDs;
	
	@OneToOne()
	@JoinColumn(name="parent_id")
	private Category parent;
	
	@OneToMany(mappedBy = "parent")
	private Set<Category> children = new HashSet<>();

	// Constructure
	public Category() {
		// TODO Auto-generated constructor stub
	}
	
	public Category(Integer id, String name) {
		this.id = id;
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}
	public Category(Integer id, String name, String alias) {
		this.id = id;
		this.name = name;
		this.alias = alias;
	}
	
	public Category(String name) {
		this.name = name;
		this.alias = name;
		this.image = "default.png";
	}
	
	public Category(String name, Category parent) {
		this(name);
		this.parent = parent;
	}
	
	public Category(Integer id) {
		this.id = id;
	}
	// methods
	public static Category copy(Category category) {
		Category newCategory = new Category();
		newCategory.setAlias(category.getAlias());
		newCategory.setChildren(category.getChildren());
		newCategory.setEnabled(category.isEnabled());
		newCategory.setId(category.getId());
		newCategory.setImage(category.getImage());
		newCategory.setName(category.getName());
		newCategory.setParent(newCategory.getParent());
		return newCategory;
	}
	
	
	// getter & setter
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Category getParent() {
		return parent;
	}

	public void setParent(Category parent) {
		this.parent = parent;
	}

	public Set<Category> getChildren() {
		return children;
	}

	public void setChildren(Set<Category> children) {
		this.children = children;
	}
	
	
	public String getAllParentIDs() {
		return allParentIDs;
	}

	public void setAllParentIDs(String allParentIDs) {
		this.allParentIDs = allParentIDs;
	}

	@Transient
	public String getImagePath() {
		return Constants.S3_BASE_URI + "/category-images/" + this.id + "/" + this.image;
	}
	@Transient
	public boolean hasChildren() {
		return this.getChildren().size() > 0;
	}
	

}
