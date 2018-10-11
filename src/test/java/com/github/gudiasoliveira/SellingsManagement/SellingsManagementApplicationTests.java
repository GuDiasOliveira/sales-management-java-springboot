package com.github.gudiasoliveira.SellingsManagement;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.fluttercode.datafactory.impl.DataFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gudiasoliveira.SellingsManagement.models.Sale;
import com.github.gudiasoliveira.SellingsManagement.models.SalesReportItem;
import com.github.gudiasoliveira.SellingsManagement.models.Seller;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SellingsManagementApplicationTests {
	
	@Autowired
    private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objMapper;
	
	private Sale genFakeSale(long sellerId, int weekDay) {
		Sale sale = new Sale();
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, (int) (Math.random() * 24));
		cal.set(Calendar.MINUTE, (int) (Math.random() * 60));
		cal.set(Calendar.SECOND, (int) (Math.random() * 60));
		cal.set(Calendar.MILLISECOND, (int) (Math.random() * 1000));
		cal.set(Calendar.DAY_OF_WEEK, weekDay);
		sale.setDate(cal.getTime());
		sale.setSellerId(sellerId);
		float value = (float) (Math.random() * 2900 + 100);
		value = (float) Math.round(value * 100) / 100;
		sale.setValue(value);
		return sale;
	}
	
	private SalesReportItem seedWeeklySales(Seller seller) throws Exception {
		SalesReportItem reportItem = new SalesReportItem();
		reportItem.setSeller(seller);
		int salesCount = 0;
		for (int weekDay = 1; weekDay <= 7; weekDay++) {
			int salesOnDay = (int) (Math.random() * 3) + 2;
			if (Math.random() >= 0.2) { // With a probability of 20%, this weekday will have no sales
				for (int i = 0; i < salesOnDay; i++) {
					Sale sale = genFakeSale(seller.getId(), weekDay);
					MvcResult mvcRes = mockMvc.perform(post("/sale/")
							.contentType(MediaType.APPLICATION_JSON_UTF8)
							.content(objMapper.writeValueAsString(sale)))
							.andExpect(status().isOk()).andReturn();
					sale = objMapper.readValue(mvcRes.getResponse().getContentAsString(), Sale.class);
					reportItem.addSale(sale);
					salesCount++;
				}
			}
			// Seed with some sales out of the current week
			// To test whether the report considers just the current week, as expected. So no addind to the reportItem
			Sale sale = genFakeSale(seller.getId(), weekDay);
			Calendar cal = Calendar.getInstance();
			cal.setTime(sale.getDate());
			cal.add(Calendar.MONTH, -2);
			sale.setDate(cal.getTime());
			mockMvc.perform(post("/sale/")
					.contentType(MediaType.APPLICATION_JSON_UTF8)
					.content(objMapper.writeValueAsString(sale)))
					.andExpect(status().isOk());
		}
		// Testing Daily Sales Average result
		assertEquals((float) salesCount / 7, reportItem.getDailySalesAverage(), 0.001);
		return reportItem;
	}
	
	public void assertReport(List<SalesReportItem> report1, List<SalesReportItem> report2) {
		for (SalesReportItem ri1 : report1) {
			for (SalesReportItem ri2 : report2) {
				if (ri1.getSeller().getId() != ri2.getSeller().getId())
					continue;
				assertEquals(ri1.getDailySalesAverage(), ri1.getDailySalesAverage(), 0.001);
			}
		}
	}
	
	private static DataFactory df = new DataFactory();
	static {
		df.randomize(new Random().nextInt());
	}
	
	private String genFakeName() {
		return df.getFirstName() + " " + df.getLastName();
	}

	@Test
	public void testSellersReport() throws Exception {
		Seller seller1 = new Seller();
		seller1.setName(genFakeName());
		Seller seller2 = new Seller();
		seller2.setName(genFakeName());
		
		MvcResult res = mockMvc.perform(post("/seller/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objMapper.writeValueAsString(seller1)))
				.andExpect(status().isOk()).andReturn();
		seller1 = objMapper.readValue(res.getResponse().getContentAsString(), Seller.class);
		
		res = mockMvc.perform(post("/seller/")
				.contentType(MediaType.APPLICATION_JSON_UTF8)
				.content(objMapper.writeValueAsString(seller2)))
				.andExpect(status().isOk()).andReturn();
		seller2 = objMapper.readValue(res.getResponse().getContentAsString(), Seller.class);
		
		SalesReportItem report1 = seedWeeklySales(seller1);
		SalesReportItem report2 = seedWeeklySales(seller2);
		
		res = mockMvc.perform(get("/salesReport/")
				.accept(MediaType.APPLICATION_JSON_UTF8))
				.andExpect(status().isOk())
				.andReturn();
		List<SalesReportItem> report = objMapper.readValue(res.getResponse().getContentAsString(),
				objMapper.getTypeFactory().constructCollectionType(List.class, SalesReportItem.class));
		
		assertReport(Arrays.asList(report1, report2), report);
	}
	
	@Test
	public void testWeekDatePeriod() throws Exception {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss:SSS");
		Date currentDate = dateFormat.parse("11/10/2018 17:56:12:133");
		Date expectedStartDate = dateFormat.parse("07/10/2018 00:00:00:000");
		Date expectedEndDate = dateFormat.parse("13/10/2018 23:59:59:999");
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(currentDate.getTime());
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		assertEquals(expectedStartDate, cal.getTime());
		
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		cal.set(Calendar.MILLISECOND, 999);
		assertEquals(expectedEndDate, cal.getTime());
	}
}
