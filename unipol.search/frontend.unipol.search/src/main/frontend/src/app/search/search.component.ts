import { Component, OnInit, ViewChild } from '@angular/core';
import { Query } from "./query";
import { SearchService } from '../search.service';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/of';
import { AutoComplete } from 'primeng/components/autocomplete/autocomplete';

@Component({
  selector: 'search-form',
  templateUrl: './search.component.html',

  styles: [
    ':host >>> .OFHighlightTerm1 { color: #0f3250; font-weight:bold }'
  ],
})
export class SearchComponent implements OnInit {
  private LOGO = require("./img/logo.jpg");
  private FOOT = require("./img/foot.png");
  query = new Query('');
  results = new Array();
  search_executed = false;
  type = false;
  suggestions = new Array();
  constructor(private searchService: SearchService) {

  }

  ngOnInit() {

    this.query = new Query('');
  }

  search(page: number) {
    console.log("Search Executed");
    let tmp_string = new Array();
    if (page < 1) {
      page = 1;
    }
    this.searchService.getSearch(this.query, page).subscribe(data => {

      this.results = new Array();

      if (data.it_type != "prev" && data.it_type == "search") {


        let result = data.es_apiResponse.es_result;


        let num_result = data.es_apiResponse.es_itemsPerPage;
        this.results[-2] = page;

        console.log("JSON risultato: " + data);
        console.log("---Numero risultati totali: " + data.es_apiResponse.es_totalResults);
        console.log("---Grandezza pagina: " + data.it_pagesize);

        //Numero di pagine stimato
        this.results[-1] = Math.ceil(data.es_apiResponse.es_totalResults / data.it_pagesize);

        console.log("In subscribe");
        console.log("Numero di pagine: " + this.results[-1]);

        //se ci sono risultati
        if (data.es_apiResponse.es_totalResults != 0) {


          if (result.constructor !== Array) {
            let result_new = new Array();
            result_new[0] = result;
            result = result_new;
            console.log("Cambio costruttore result: " + (result.constructor == Array));
          }
          
          for (var _i = 0; _i < num_result; _i++) {
            this.results[_i] = [];
            //data.es_apiResponse.es_result
            this.results[_i]["summary"] = result[_i].es_summary;

            let links = result[_i].es_link;

            if (links.constructor !== Array) {
              let links_new = Array();
              links_new[0] = links;
              links = links_new;
              console.log("Cambio costruttore link: " + (links.constructor == Array));
            }
      
            this.results[_i]["link"] = links[0].href;
            tmp_string = links[0].href.split('/');
            console.log(result[_i].es_title);
            if (result[_i].es_title != null) {
              this.results[_i]["title"] = result[_i].es_title;
            } else {
              this.results[_i]["title"] = tmp_string[tmp_string.length - 1];
            }
          }
        }
        this.type = true;
      } else if (data.es_apiResponse.type == "auto") {
        this.results["date"] = data.es_apiResponse.date;
        this.results["piva"] = data.es_apiResponse.piva;
        this.results["email"] = data.es_apiResponse.email;
        this.results["targa"] = data.es_apiResponse.targa;
        this.results["kmanno"] = data.es_apiResponse.kmanno;
        this.type = false;
      } else {
        /*TO DO!! preventivatore non supportato*/
      }
      this.search_executed = true;

    });
  }





  @ViewChild(AutoComplete) autocomplete: AutoComplete;
  suggestion() {
    console.log("Suggestion Executed");
    let pos = this.autocomplete.input.selectionStart;
    console.log("position" + pos);
    this.searchService.getSuggestions(this.query).subscribe(data => {
      let result = data.es_apiResponse.es_queryTypeAhead;
      let sug;
      let sug_length;
      if (result.constructor !== Array) {
        console.log(1);
        console.log(data);
        sug = result;
        this.suggestions = new Array();
        if (sug.es_suggestion.constructor !== Array) {
          console.log(3);
          this.suggestions[0] = sug.es_suggestion.text;
        }
        else {
          console.log(4);
          for (var _i = 0; _i < sug.es_suggestion.length; _i++) {
            this.suggestions[_i] = sug.es_suggestion[_i].text;
          }

        }
      }
      else {
        console.log(2);
        sug_length = result.length;
        sug = result;
        console.log(data);
        this.suggestions = new Array();
        for (var _z = 0; _z < sug_length; _z++) {
          console.log(sug[_z]);
          if (sug[_z].es_suggestion.constructor !== Array) {
            console.log(5);
            this.suggestions[0] = sug[_z].es_suggestion.text;
          }
          else {
            console.log(6);
            for (var _i = 0; _i < sug[_z].es_suggestion.length; _i++) {
              this.suggestions[_i] = sug[_z].es_suggestion[_i].text;
            }

          }
        }
      }

      console.log(this.suggestions);

    }

    );
    setTimeout(() => { console.log("pos " + pos); console.log("START " + this.autocomplete.input.selectionStart); console.log("END " + this.autocomplete.input.selectionEnd); this.autocomplete.input.selectionEnd = pos });
  }

}