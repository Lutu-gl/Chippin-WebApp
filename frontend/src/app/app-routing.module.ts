import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import { FriendsComponent } from './components/friends/friends.component';
import { AddFriendComponent } from './components/add-friend/add-friend.component';
import {PantryComponent} from "./components/pantry/pantry.component";
import {RegisterComponent} from "./components/register/register.component";
import {GroupListComponent} from "./components/group-list/group-list.component";
import {GroupCreateComponent, GroupCreateEditMode} from "./components/group-list/group-create/group-create.component";
import {GroupInfoComponent} from "./components/group-list/group-info/group-info.component";
import {RecipeComponent} from "./components/recipe/recipe.component";
import {ItemListComponent} from "./components/item-list/item-list.component";
import {
  RecipeCreateEditComponent,
  RecipeCreateEditMode
} from "./components/recipe/recipe-create-edit/recipe-create-edit.component";
import {
  ItemListCreateEditComponent, ItemListCreateEditMode
} from "./components/item-list/item-list-create-edit/item-list-create-edit.component";

import {
  ShoppingListCreateComponent, ShoppingListCreateEditMode
} from "./components/shopping-list/shopping-list-create/shopping-list-create.component";
import {
  ShoppingListDetailComponent
} from "./components/shopping-list/shopping-list-detail/shopping-list-detail.component";
import { ExpenseCreateComponent, ExpenseCreateEditMode } from './components/expense/expense-create/expense-create.component';


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'group', canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: 'create', component: GroupCreateComponent, data: {mode: GroupCreateEditMode.create}},
      {path: '', component: GroupListComponent},
      {path: ':id', children: [
          {path: '', component: GroupInfoComponent},
          {path: 'pantry', component: PantryComponent},
          {path: 'edit', component: GroupCreateComponent, data: {mode: GroupCreateEditMode.edit}},
          {path: 'shoppingList', children: [
              {path: 'create', component: ShoppingListCreateComponent, data: {mode: ShoppingListCreateEditMode.create}},
              {path: ':shoppingListId', children: [
                  {path: '', component: ShoppingListDetailComponent},
                  {path: 'edit', component: ShoppingListCreateComponent, data: {mode: ShoppingListCreateEditMode.edit}},
                ]}
            ]}
        ]},
    ]},
  {path: 'register', component: RegisterComponent},
  {path: 'friends', canActivate: mapToCanActivate([AuthGuard]), component: FriendsComponent},
  {path: 'add-friend', canActivate: mapToCanActivate([AuthGuard]), component: AddFriendComponent},
  //May need to change if recipe is dependent on group
  {path: 'recipe', canActivate: mapToCanActivate([AuthGuard]), component: RecipeComponent},
  {path: 'recipe/create', canActivate: mapToCanActivate([AuthGuard]), component:RecipeCreateEditComponent, data: {mode: RecipeCreateEditMode.create}},
  {path: 'recipe/:id/edit', canActivate: mapToCanActivate([AuthGuard]), component:RecipeCreateEditComponent, data: {mode: RecipeCreateEditMode.edit}},
  {path: 'itemList', component: ItemListComponent},
  {path: 'itemList/create', canActivate: mapToCanActivate([AuthGuard]), component:ItemListCreateEditComponent, data: {mode: ItemListCreateEditMode.create}},
  {path: 'itemList/:id/edit', canActivate: mapToCanActivate([AuthGuard]), component:ItemListCreateEditComponent, data: {mode: ItemListCreateEditMode.edit}},
  {path: 'expenses', canActivate: mapToCanActivate([AuthGuard]), component:ExpenseCreateComponent, data: {mode: ExpenseCreateEditMode.create}},
  {path: 'expenses/:id', canActivate: mapToCanActivate([AuthGuard]), component:ExpenseCreateComponent, data: {mode: ExpenseCreateEditMode.create}},
  {path: 'expenses/info/:id', canActivate: mapToCanActivate([AuthGuard]), component:ExpenseCreateComponent, data: {mode: ExpenseCreateEditMode.info}},
  {path: 'expenses/edit/:id', canActivate: mapToCanActivate([AuthGuard]), component:ExpenseCreateComponent, data: {mode: ExpenseCreateEditMode.edit}},
  {path: '**', redirectTo: ''},




];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
