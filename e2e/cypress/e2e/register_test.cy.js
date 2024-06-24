describe('Registration Page Negative Test', () => {
  it('should disable the register button when form inputs are invalid', () => {
    cy.visit('/#/register');

    cy.get('#inputEmail').clear().type('invalid-email');

    cy.get('#inputPassword').clear().type('123').click();

    cy.get('body').click(0, 0);

    cy.get('#inputConfirmPassword').type('123');

    cy.get('body').click(0, 0); 

    cy.get('p-button').should('have.attr', 'ng-reflect-disabled', 'true');
    cy.get('p-button button').should('be.disabled');
  });
});


describe('Registration Page Positive Test', () => {
  it('should enable the register button when all form inputs are valid', () => {
    cy.visit('/#/register');

    cy.get('#inputEmail').clear().type('test@example.com');

    cy.get('#inputPassword').clear().type('Password123!');
	
	cy.get('body').click(0, 0);

    cy.get('#inputConfirmPassword').type('Password123!');

    cy.get('body').click(0, 0); 

    cy.get('div.p-password-panel', { timeout: 5000 }).should('not.exist');

    cy.get('p-button').should('not.have.attr', 'ng-reflect-disabled', 'true');
    cy.get('p-button button').should('not.be.disabled');
  });
});
