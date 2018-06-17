package io.github.u2ware.apps;

//import static org.hamcrest.CoreMatchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
//import org.springframework.hateoas.MediaTypes;
//import org.springframework.http.HttpHeaders;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Application.class)
public class ApplicationTests {

	protected Log logger = LogFactory.getLog(getClass());

	private MockMvc mvc;
	private @Autowired WebApplicationContext context;
	
	@Before
	public void setUp(){
		this.mvc = MockMvcBuilders.webAppContextSetup(context).build() ;
	}

	@Test
	public void contextLoads() throws Exception {
		
		this.mvc.perform(
				get("/apis")
			).andDo(
				print()
			).andExpect(
				status().is2xxSuccessful()
			);
		
		String [] a1 = context.getBeanNamesForType(DataSource.class);
		for(String n : a1){
			logger.info("DataSource: "+n);
		}
		String [] a3 = context.getBeanNamesForType(EntityManagerFactory.class);
		for(String n : a3){
			logger.info("EntityManagerFactory: "+n);
		}
		
		String [] a4 = context.getBeanNamesForType(AuditingHandler.class);
		for(String n : a4){
			logger.info("AuditingHandler: "+n);
		}
		
		String [] a5 = context.getBeanNamesForType(PersistentEntities.class);
		for(String n : a5){
			logger.info("PersistentEntities: "+n);
		}
		
		String [] a6 = context.getBeanNamesForType(ConversionService.class);
		for(String n : a6){
			logger.info("ConversionService: "+n);
		}
	}
}
