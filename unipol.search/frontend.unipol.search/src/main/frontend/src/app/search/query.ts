export class Query {
  constructor(
    public query: string
  ) {  }
  
  getVal()
  {
  console.log(this.query);
  }
}