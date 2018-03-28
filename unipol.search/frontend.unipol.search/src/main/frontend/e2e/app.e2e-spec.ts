import { UnipolSearchPage } from './app.po';

describe('unipol-search App', function() {
  let page: UnipolSearchPage;

  beforeEach(() => {
    page = new UnipolSearchPage();
  });

  it('should display message saying app works', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('app works!');
  });
});
