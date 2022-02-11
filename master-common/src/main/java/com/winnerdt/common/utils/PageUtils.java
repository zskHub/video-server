package com.winnerdt.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分页工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2016年11月4日 下午12:59:00
 */
public class PageUtils implements Serializable {
	private static final long serialVersionUID = 1L;
	//总记录数
	private int totalCount;
	//每页记录数
	private int pageSize;
	//总页数
	private int totalPage;
	//当前页数
	private int currPage;
	//列表数据
	private List<?> list;
	private Map pagination;

	/**
	 * 分页
	 * @param list        列表数据
	 * @param totalCount  总记录数
	 * @param pageSize    每页记录数
	 * @param currPage    当前页数
	 */
	public PageUtils(List<?> list, int totalCount, int pageSize, int currPage) {
		Map map = new HashMap();
		map.put("current",currPage);
		map.put("pageSize",pageSize);
		map.put("total",totalCount);

		this.list = list;
		this.pagination = map;

	}

	/**
	 * 分页
	 */
	public PageUtils(Page<?> page) {
		Map map = new HashMap();
		map.put("current",page.getCurrent());
		map.put("pageSize",page.getSize());
		map.put("total",page.getTotal());
		this.pagination = map;
		this.list = page.getRecords();
	}

	public Map getPagination() {
		return pagination;
	}

	public void setPagination(Map pagination) {
		this.pagination = pagination;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	public int getCurrPage() {
		return currPage;
	}

	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}

}
