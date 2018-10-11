package com.github.gudiasoliveira.SellingsManagement.controllers;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.gudiasoliveira.SellingsManagement.dao.SaleDao;
import com.github.gudiasoliveira.SellingsManagement.models.Sale;
import com.github.gudiasoliveira.SellingsManagement.models.SalesReportItem;

@RestController
public class SalesReportController {
	
	@Autowired
	SaleDao saleDao;
	
	@GetMapping("/salesReport")
	public List<SalesReportItem> salesReport() {
		/* Making week calendar datetime limit */
		Calendar cal = Calendar.getInstance();
		// Beginning of the week
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Date startDate = new Date(cal.getTimeInMillis());
		// End of the week
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		Date endDate = new Date(cal.getTimeInMillis());
		
		Iterable<Sale> weekSales = saleDao.findByDateBetween(startDate, endDate);
		return SalesReportItem.makeSalesReport(weekSales, 10);
	}
}
