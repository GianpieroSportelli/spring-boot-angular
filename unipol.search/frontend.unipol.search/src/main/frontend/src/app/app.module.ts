import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { JsonpModule } from '@angular/http';
import { AutoCompleteModule } from 'primeng/primeng';

import { AppComponent } from './app.component';
import { SearchComponent } from './search/search.component';

import { SearchService } from './search.service';

@NgModule({
  declarations: [
    AppComponent, SearchComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    JsonpModule,
    AutoCompleteModule
  ],
  providers: [SearchService],
  bootstrap: [AppComponent]
})
export class AppModule { }
