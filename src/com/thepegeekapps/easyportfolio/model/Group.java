package com.thepegeekapps.easyportfolio.model;

import java.util.List;

public class Group extends Item {
	
	protected List<Portfolio> portfolios;
	
	public Group(int id, String name, List<Portfolio> portfolios) {
		setId(id);
		setName(name);
		setPortfolios(portfolios);
	}
	
	public List<Portfolio> getPortfolios() {
		return portfolios;
	}
	
	public void setPortfolios(List<Portfolio> portfolios) {
		this.portfolios = portfolios;
	}

	@Override
	public int getType() {
		return TYPE_GROUP;
	}

}
