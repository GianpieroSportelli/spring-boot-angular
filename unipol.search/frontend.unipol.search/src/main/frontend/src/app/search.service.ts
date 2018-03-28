import { Injectable } from '@angular/core';
import { Http, Headers, RequestOptions } from '@angular/http';
import { Jsonp } from '@angular/http';
import { Observable } from "rxjs/Rx";
import { Query } from "./search/query";
import 'rxjs/add/operator/map'

const host = "centos123.reply.it";//:8080
const url_search = "http://" + host + "/searchAPI";
const url_sugg = "http://" + host + "/suggestionAPI";

@Injectable()
export class SearchService {

  constructor(private http: Http, private jsonp: Jsonp) { }

  getSearch(query: Query, page: number) {
    console.log(query.query);
    console.log(page);
    let body;
    if (query.query != "") {
      body = "input=" + query.query + "&page=" + page;
    }
    else {
      body = "input=*:*&page=" + page;
    }
    let headers = new Headers({ 'Content-Type': 'application/x-www-form-urlencoded' });
    let options = new RequestOptions({ headers: headers });
    return this.http.post(url_search, body, options).map(response => response.json());
  }

  getSuggestions(query: Query) {
    console.log(query.query);

    let body = "input=" + query.query;
    let headers = new Headers({ 'Content-Type': 'application/x-www-form-urlencoded' });
    let options = new RequestOptions({ headers: headers });

    return this.http.post(url_sugg, body, options).map(response => response.json());
  }

}
