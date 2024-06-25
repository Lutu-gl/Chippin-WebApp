describe('Landing Page Test', () => {
  it('The start page should load successfully and display a specific text', () => {
    cy.visit('/'); 

    cy.contains('Chippin').should('be.visible');
  });
});

