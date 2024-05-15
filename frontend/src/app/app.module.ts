import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {HeaderComponent} from './components/header/header.component';
import {FooterComponent} from './components/footer/footer.component';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {httpInterceptorProviders} from './interceptors';
import { FriendsComponent } from './components/friends/friends.component';
import { AddFriendComponent } from './components/add-friend/add-friend.component';
import {GroupListComponent} from "./components/group-list/group-list.component";
import {GroupCreateComponent} from "./components/group-list/group-create/group-create.component";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {ToastrModule} from "ngx-toastr";
import {AutocompleteComponent} from "./components/autocomplete/autocomplete.component";
import {GroupInfoComponent} from "./components/group-list/group-info/group-info.component";
import {ItemListComponent} from "./components/item-list/item-list.component";
import {RecipeComponent} from "./components/recipe/recipe.component";
import {RecipeCreateEditComponent} from "./components/recipe/recipe-create-edit/recipe-create-edit.component";
import {
  ItemListCreateEditComponent
} from "./components/item-list/item-list-create-edit/item-list-create-edit.component";

import {ShoppingListComponent} from "./components/shopping-list/shopping-list.component";
import { ExpenseCreateComponent } from './components/expense/expense-create/expense-create.component';
import { ExpenseListComponent } from './components/expense/expense-list.component';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    AutocompleteComponent,
    LoginComponent,
    FriendsComponent,
    AddFriendComponent,
    GroupListComponent,
    GroupCreateComponent,
    GroupInfoComponent,
    ItemListComponent,
    ItemListCreateEditComponent,
    RecipeComponent,
    RecipeCreateEditComponent,
    ExpenseCreateComponent,
    ExpenseListComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    FormsModule,
    BrowserAnimationsModule, ToastrModule.forRoot(), ShoppingListComponent
  ],
  providers: [httpInterceptorProviders],
  bootstrap: [AppComponent]
})
export class AppModule {
}
