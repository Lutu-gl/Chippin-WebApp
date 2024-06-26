describe('Login Page Test Negativ', () => {
  it('should disable the login button when form inputs are invalid', () => {
    cy.visit('/#/login');

    cy.get('#inputEmail').clear();
    cy.get('#inputPassword').clear();

    cy.get('p-button').should('have.attr', 'ng-reflect-disabled', 'true');

    cy.get('p-button button').should('be.disabled');
  });
});
  
describe('Login Page Test Positive', () => {
  it('Login button becomes active when email and password are provided', () => {
	  cy.visit('/#/login');
	  cy.get('#inputEmail').type('test@example.com');

	  cy.get('#inputPassword').type('password123');

	  cy.get('p-button').should('not.be.disabled');
	});
});

