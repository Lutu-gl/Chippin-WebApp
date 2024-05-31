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
import {RecipeCreateComponent} from "./components/recipe/recipe-create/recipe-create.component";
import {
  ItemListCreateEditComponent
} from "./components/item-list/item-list-create-edit/item-list-create-edit.component";

import {ShoppingListComponent} from "./components/shopping-list/shopping-list.component";
import { BudgetListComponent } from './components/budget/budget-list.component';
import { BudgetCreateComponent } from './components/budget/budget-create/budget-create.component';
import { ExpenseCreateComponent } from './components/expense/expense-create/expense-create.component';
import { ExpenseListComponent } from './components/expense/expense-list.component';
import { ConfirmDeleteDialogComponent } from './components/confirm-delete-dialog/confirm-delete-dialog.component';
import {MenuCardComponent} from "./components/menu-cards/menu-card/menu-card.component";
import {
  GroupInfoCardContentComponent
} from "./components/group-list/group-info-card-content/group-info-card-content.component";
import {
  FriendsInfoCardContentComponent
} from "./components/friends/friends-info-card-content/friends-info-card-content.component";
import {
  ItemListInfoCardContentComponent
} from "./components/item-list/item-list-info-card-content/item-list-info-card-content.component";
import {
  RecipeInfoCardContentComponent
} from "./components/recipe/recipe-info-card-content/recipe-info-card-content.component";
import {RecipeDetailComponent} from "./components/recipe/recipe-detail/recipe-detail.component";
import {RecipeEditComponent} from "./components/recipe/recipe-edit/recipe-edit.component";
import {RecipeGlobalComponent} from "./components/recipe/recipe-global/recipe-global.component";
import {PaymentCreateComponent} from "./components/payment-create/payment-create.component";
import {
  ShoppingListCreateComponent
} from "./components/shopping-list/shopping-list-create/shopping-list-create.component";
import {
  ShoppingListInfoCardContentComponent
} from "./components/shopping-list/shopping-list-info-card-content/shopping-list-info-card-content.component";
import { FriendInfoComponent } from './components/friends/friend-info/friend-info.component';
import { ExpenseInfoCardContentComponent } from './components/expense/expense-info-card-content/expense-info-card-content.component';
import {RecipeLikedComponent} from "./components/recipe/recipe-liked/recipe-liked.component";
import {SplitButtonModule} from "primeng/splitbutton";
import {MenuModule} from "primeng/menu";
import {ChipsModule} from "primeng/chips";
import {FloatLabelModule} from "primeng/floatlabel";
import {PasswordModule} from "primeng/password";
import {DividerModule} from "primeng/divider";
import {AutoFocusModule} from "primeng/autofocus";
import {ToastModule} from "primeng/toast";
import {MessageService} from "primeng/api";
import {TabMenuModule} from 'primeng/tabmenu';
import {AutoCompleteModule} from "primeng/autocomplete";

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    HomeComponent,
    AutocompleteComponent,
    LoginComponent,
    FriendsComponent,
    FriendInfoComponent,
    AddFriendComponent,
    GroupListComponent,
    GroupCreateComponent,
    GroupInfoComponent,
    ItemListComponent,
    ItemListCreateEditComponent,
    RecipeComponent,
    ExpenseCreateComponent,
    ExpenseListComponent,
    BudgetListComponent,
    BudgetCreateComponent,
    RecipeCreateComponent,
    RecipeDetailComponent,
    RecipeEditComponent,
    RecipeGlobalComponent,
    RecipeLikedComponent,
    PaymentCreateComponent,
    ShoppingListCreateComponent
  ],

  imports: [
      BrowserModule,
      AppRoutingModule,
      ReactiveFormsModule,
      HttpClientModule,
      NgbModule,
      FormsModule,
      BrowserAnimationsModule, ToastrModule.forRoot(),
      ShoppingListComponent,
      BrowserAnimationsModule,
      ToastrModule.forRoot(),
      ShoppingListComponent,
      MenuCardComponent,
      GroupInfoCardContentComponent,
      FriendsInfoCardContentComponent,
      ExpenseInfoCardContentComponent,
      ItemListInfoCardContentComponent,
      RecipeInfoCardContentComponent,
      ConfirmDeleteDialogComponent, ShoppingListInfoCardContentComponent, SplitButtonModule, MenuModule, ChipsModule, FloatLabelModule, PasswordModule, DividerModule, AutoFocusModule, ToastModule, TabMenuModule, AutoCompleteModule
  ],

  providers: [httpInterceptorProviders, MessageService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
