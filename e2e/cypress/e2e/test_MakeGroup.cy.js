describe("Create a Group", () => {
  it("Create a group with two members", () => {
    cy.viewport(1903, 495);
    cy.visit("http://localhost:4200/#/");
    cy.get("button.p-button-primary > span").click();
    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tfa@email.com");
	
    cy.get("div:nth-of-type(2) input").type("Password123");
	cy.get('body').click(0, 0);
    cy.get("div:nth-of-type(3) input").type("Password123");
	
    cy.get("form").click();
    cy.get("button").click();
    cy.get("app-header button").click();
	
    cy.contains('span', 'Logout').click();
    cy.get("div a").click();
    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tf2a@email.com");
    cy.get("div:nth-of-type(2) input").type("Password123!");
	cy.get('body').click(0, 0);
    cy.get("div:nth-of-type(3) input").type("Password123!");
    cy.get("app-register > div > div").click();
    cy.get("button").click();
	cy.get("app-header button").click();
    cy.contains('span', 'Logout').click();
	
	cy.visit("http://localhost:4200/#/");
    cy.visit("http://localhost:4200/#/login");
    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tfa@email.com");
    cy.get("div:nth-of-type(2) input").click();
    cy.get("div:nth-of-type(2) input").type("Password123");
    cy.get("button").click();
    cy.get("app-root > div button").click();
	
    cy.contains('p-button', 'Send Friend Request').click({ force: true });
    cy.get("#email").type("tf2a@email.com");
    cy.get("p-button:nth-of-type(2) > button").click();
    cy.get("app-header button").click();
    cy.contains('span', 'Logout').click();

    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tf2a@email.com");
    cy.get("div:nth-of-type(2) input").click();
    cy.get("div:nth-of-type(2) input").type("Password123!");
    cy.get("div:nth-of-type(3)").click();
    cy.get("span.p-button-label").click();
    cy.get("button.bg-green-500").click();
	
	cy.visit("http://localhost:4200/#/home/friends");
    cy.get("li:nth-of-type(2) > a").click();
    cy.get("app-root > div button").click();
    cy.get("#inputGroupName").click();
    cy.get("#inputGroupName").type("Test");
    cy.get("#inputMembers input").click();
    cy.get("#inputMembers input").type("tfa@email.com");
	cy.contains("div", "tfa@email.com").click();
	
    cy.get("p-button:nth-of-type(2) > button").click();
  });
});
