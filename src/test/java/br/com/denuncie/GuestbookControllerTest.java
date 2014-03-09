package br.com.denuncie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.users.User;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalUserServiceTestConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:**/application-context.xml")
@WebAppConfiguration
public class GuestbookControllerTest {
	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	private final LocalServiceTestHelper localServiceHelper = new LocalServiceTestHelper(
			new LocalUserServiceTestConfig(),
			new LocalDatastoreServiceTestConfig());

	private final String userEmail = "test@guestbook.com";
	private final String userDomain = "guestbook.com";
	private final String userNickname = "test";

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
		localServiceHelper.setUp();
	}

	@After
	public void tearDown() {
		localServiceHelper.tearDown();
	}

	@Test
	public void testListingLoggedIn() throws Exception {
		loginUser();

		ModelAndView mav = mockMvc.perform(get("/loggedIn"))
				.andExpect(status().isOk()).andReturn().getModelAndView();
		ModelAndViewAssert.assertModelAttributeValue(mav, "welcomeMsg",
				"You are authenticated, " + userNickname);
	}

	@Test
	public void testListingNotLoggedIn() throws Exception {
		logoutUser();

		// Moved to authentication service
		mockMvc.perform(get("/loggedIn")).andExpect(
				status().isMovedTemporarily());
	}

	@Test
	public void testSignGuestbookLoggedIn() throws Exception {
		loginUser();
		signGuestbook();

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		// Throws exception if more than one is found
		Key guestbookKey = KeyFactory.createKey("Guestbook", "default");
		Entity entity = ds.prepare(
				new Query("Greeting", guestbookKey)
						.setFilter(new FilterPredicate("user",
								FilterOperator.EQUAL, new User(userEmail,
										userDomain)))).asSingleEntity();

		Assert.assertNotNull(entity);
	}

	@Test
	public void testSignGuestbookLoggedOut() throws Exception {
		logoutUser();
		signGuestbook();

		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

		// Throws exception if more than one is found
		Key guestbookKey = KeyFactory.createKey("Guestbook", "default");
		Entity entity = ds.prepare(
				new Query("Greeting", guestbookKey)
						.setFilter(new FilterPredicate("user",
								FilterOperator.EQUAL, null))).asSingleEntity();

		Assert.assertNotNull(entity);
	}

	private void signGuestbook() throws Exception {
		ModelAndView mav = mockMvc
				.perform(
						get("/sign").param("guestbookName", "default").param(
								"content", "Inserting new greeting"))
				.andExpect(status().isOk()).andReturn().getModelAndView();
		ModelAndViewAssert.assertModelAttributeValue(mav, "guestbookName",
				"default");
	}

	private void loginUser() {
		localServiceHelper.setEnvIsLoggedIn(true).setEnvEmail(userEmail)
				.setEnvAuthDomain(userDomain);
	}

	private void logoutUser() {
		localServiceHelper.setEnvIsLoggedIn(false);
	}
}
