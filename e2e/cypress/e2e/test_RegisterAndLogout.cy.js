describe("Register User and Logout", () => {
  it("register and then logout should work", () => {
    cy.viewport(1903, 495);
    cy.visit("http://localhost:4200/#/login");
    cy.visit("http://localhost:4200/#/");
    cy.get("button.p-button-primary > span").click();
    cy.get("#inputEmail").click();
    cy.get("#inputEmail").type("t3@example.com");
    cy.get("div:nth-of-type(2) input").click();
    cy.get("div:nth-of-type(2) input").type("Password123!");
	cy.get('body').click(0, 0);
    cy.get("app-register > div > div").click();
    cy.get("div:nth-of-type(3) input").click();
    cy.get("div:nth-of-type(3) input").type("Password123!");
    cy.get("app-register > div > div").click();
    cy.get("button").click();
    cy.get("li:nth-of-type(2) > a").click();
    cy.get("li:nth-of-type(3) > a").click();
    cy.get("li:nth-of-type(4) > a").click();
    cy.get("app-header button").click();
	cy.contains('span', 'Logout').click();
  });
});
