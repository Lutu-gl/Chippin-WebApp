describe("Create two Users And Befriend them", () => {
  it("should make two users and send friend request from one and accept friend request from the other", () => {
    cy.viewport(1903, 495);
    cy.visit("http://localhost:4200/#/");
    cy.get("button.p-button-primary > span").click();
    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tf@email.com");
	
    cy.get("div:nth-of-type(2) input").type("Password123");
	cy.get('body').click(0, 0);
    cy.get("div:nth-of-type(3) input").type("Password123");
	
    cy.get("form").click();
    cy.get("button").click();
    cy.get("app-header button").click();
	
    cy.contains('span', 'Logout').click();
    cy.get("div a").click();
    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tf2@email.com");
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
    cy.get("#inputEmail").type("tf@email.com");
    cy.get("div:nth-of-type(2) input").click();
    cy.get("div:nth-of-type(2) input").type("Password123");
    cy.get("button").click();
    cy.get("app-root > div button").click();
	
    cy.get('.text-black > .p-ripple').click();
    cy.get("#email").type("tf2@email.com");
    cy.get("p-button:nth-of-type(2) > button").click();
    cy.get("app-header button").click();
    cy.contains('span', 'Logout').click();

    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("tf2@email.com");
    cy.get("div:nth-of-type(2) input").click();
    cy.get("div:nth-of-type(2) input").type("Password123!");
    cy.get("div:nth-of-type(3)").click();
    cy.get("span.p-button-label").click();
    cy.get("button.bg-green-500").click();
	
  });
});
