package com.github.gudiasoliveira.SellingsManagement.models;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SalesReportItem {
	private Seller seller;
	private Map<Date, List<Sale>> sales = new HashMap<>();
	private int totalSales = 0;
	
	public Seller getSeller() {
		return seller;
	}
	
	public void setSeller(Seller seller) {
		this.seller = seller;
	}
	
	public void addSale(Date date, Sale sale) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		date = cal.getTime();
		if (!sales.containsKey(date)) {
			sales.put(date, new ArrayList<>());
		}
		sales.get(date).add(sale);
		totalSales++;
	}
	
	public void addSale(Sale sale) {
		addSale(sale.getDate(), sale);
	}
	
	public float getDailySalesAverage() {
		return (float) totalSales / sales.size();
	}
	
	public static List<SalesReportItem> makeSalesReport(Iterable<Sale> sales, int rankLimit) {
		List<SalesReportItem> report = new ArrayList<>();
		for (Sale sale : sales) {
			Seller seller = sale.getSeller();
			SalesReportItem current = null;
			for (SalesReportItem item : report) {
				if (item.getSeller().getId() == seller.getId()) {
					current = item;
					break;
				}
			}
			if (current == null) {
				current = new SalesReportItem();
				current.setSeller(seller);
				report.add(current);
			}
			current.addSale(sale.getDate(), sale);
			if (rankLimit > 0 && report.size() >= rankLimit)
				break;
		}
		Collections.sort(report, new Comparator<SalesReportItem>() {
			@Override
			public int compare(SalesReportItem lreport, SalesReportItem rreport) {
				float diff = lreport.getDailySalesAverage() - rreport.getDailySalesAverage();
				if (diff > 0)
					return -1;
				if (diff < 0)
					return 1;
				return 0;
			}
		});
		return report;
	}
}
