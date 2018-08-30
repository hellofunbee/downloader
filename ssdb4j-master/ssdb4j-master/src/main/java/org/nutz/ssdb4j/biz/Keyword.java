package org.nutz.ssdb4j.biz;

import java.util.Date;

public class Keyword {
	private String keyword;

	private Date update;

	private Date insertDate;

	private int searchCount;

	public Date getInsertDate() {
		return insertDate;
	}

	public String getKeyword() {
		return keyword;
	}

	public int getSearchCount() {
		return searchCount;
	}

	public Date getUpdate() {
		return update;
	}

	public void setInsertDate(Date insertDate) {
		this.insertDate = insertDate;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setSearchCount(int searchCount) {
		this.searchCount = searchCount;
	}

	public void setUpdate(Date update) {
		this.update = update;
	}
}
