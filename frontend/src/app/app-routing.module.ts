import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {FriendsComponent} from './components/friends/friends.component';
import {AddFriendComponent} from './components/add-friend/add-friend.component';
import {PantryComponent} from "./components/pantry/pantry.component";
import {RegisterComponent} from "./components/register/register.component";
import {GroupListComponent} from "./components/group-list/group-list.component";
import {GroupCreateComponent, GroupCreateEditMode} from "./components/group-list/group-create/group-create.component";
import {GroupInfoComponent} from "./components/group-list/group-info/group-info.component";
import {RecipeComponent} from "./components/recipe/recipe.component";
import {
  RecipeCreateComponent
} from "./components/recipe/recipe-create/recipe-create.component";

import {
  ShoppingListCreateComponent, ShoppingListCreateEditMode
} from "./components/shopping-list/shopping-list-create/shopping-list-create.component";
import {
  ShoppingListDetailComponent
} from "./components/shopping-list/shopping-list-detail/shopping-list-detail.component";

import {BudgetCreateComponent, BudgetCreateEditMode} from './components/budget/budget-create/budget-create.component';
import {
  ExpenseCreateComponent,
  ExpenseCreateEditMode
} from './components/expense/expense-create/expense-create.component';
import {RecipeDetailComponent, RecipeDetailMode} from "./components/recipe/recipe-detail/recipe-detail.component";
import {RecipeEditComponent} from "./components/recipe/recipe-edit/recipe-edit.component";
import {RecipeGlobalComponent} from "./components/recipe/recipe-global/recipe-global.component";
import {PaymentCreateComponent, PaymentCreateEditMode} from "./components/payment-create/payment-create.component";
import {FriendInfoComponent} from './components/friends/friend-info/friend-info.component';
import {RecipeLikedComponent} from "./components/recipe/recipe-liked/recipe-liked.component";
import {HomeComponent} from "./components/home/home.component";
import {VisualizationComponent} from "./components/visualization/visualization.component";
import {SettingsComponent} from './components/settings/settings.component';
import {LandingPageComponent} from './components/landing-page/landing-page.component';


let routes: Routes;
routes = [
  {path: '', component: LandingPageComponent},
  {path: 'login', component: LoginComponent},
  {path: 'settings', canActivate: mapToCanActivate([AuthGuard]), component: SettingsComponent},
  {
    path: "shopping-list", canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: 'create', component: ShoppingListCreateComponent, data: {mode: ShoppingListCreateEditMode.create}},
      {
        path: ':shoppingListId', children: [
          {path: '', component: ShoppingListDetailComponent},
          {path: 'edit', component: ShoppingListCreateComponent, data: {mode: ShoppingListCreateEditMode.edit}},
        ]
      }
    ]
  },
  {
    path: 'group', canActivate: mapToCanActivate([AuthGuard]), children: [
      {path: 'create', component: GroupCreateComponent, data: {mode: GroupCreateEditMode.create}},
      {path: '', component: GroupListComponent},
      {
        path: ':id', children: [
          {path: '', component: GroupInfoComponent},
          {path: 'budgets/create', component: BudgetCreateComponent, data: {mode: BudgetCreateEditMode.create}},
          {path: 'budgets/info/:budgetId', component: BudgetCreateComponent, data: {mode: BudgetCreateEditMode.info}},
          {path: 'pantry', component: PantryComponent},
          {path: 'edit', component: GroupCreateComponent, data: {mode: GroupCreateEditMode.edit}},
          {path: 'visualization', component: VisualizationComponent},
          {
            path: 'payment', children: [
              {
                path: 'create/:email/:amount',
                component: PaymentCreateComponent,
                data: {mode: ExpenseCreateEditMode.create}
              },
              {
                path: ':paymentId', children: [
                  {path: 'info', component: PaymentCreateComponent, data: {mode: PaymentCreateEditMode.info}},
                  {path: 'edit', component: PaymentCreateComponent, data: {mode: PaymentCreateEditMode.edit}}
                ]
              }
            ]
          },
          {
            path: 'shoppingList', children: [
              {path: 'create', component: ShoppingListCreateComponent, data: {mode: ShoppingListCreateEditMode.create}},
              {
                path: ':shoppingListId', children: [
                  {path: '', component: ShoppingListDetailComponent},
                  {path: 'edit', component: ShoppingListCreateComponent, data: {mode: ShoppingListCreateEditMode.edit}},
                ]
              }
            ]
          }
        ]
      },
    ]
  },
  {path: 'register', component: RegisterComponent},
  {path: 'friends', canActivate: mapToCanActivate([AuthGuard]), component: FriendsComponent},
  {path: 'friends/:email', canActivate: mapToCanActivate([AuthGuard]), component: FriendInfoComponent},
  {path: 'add-friend', canActivate: mapToCanActivate([AuthGuard]), component: AddFriendComponent},
  {path: 'recipe', canActivate: mapToCanActivate([AuthGuard]), component: RecipeComponent},
  {path: 'recipe/create', canActivate: mapToCanActivate([AuthGuard]), component: RecipeCreateComponent},
  {
    path: 'recipe/owner/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: RecipeDetailComponent,
    data: {mode: RecipeDetailMode.owner}
  },
  {
    path: 'recipe/viewer/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: RecipeDetailComponent,
    data: {mode: RecipeDetailMode.viewer}
  },
  {path: 'recipe/edit/:id', canActivate: mapToCanActivate([AuthGuard]), component: RecipeEditComponent},
  {path: 'recipe/global', canActivate: mapToCanActivate([AuthGuard]), component: RecipeGlobalComponent},
  {path: 'recipe/liked', canActivate: mapToCanActivate([AuthGuard]), component: RecipeLikedComponent},
  ,
  {
    path: 'expenses/create',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ExpenseCreateComponent,
    data: {mode: ExpenseCreateEditMode.create}
  },
  {
    path: 'expenses/create/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ExpenseCreateComponent,
    data: {mode: ExpenseCreateEditMode.create}
  },
  {
    path: 'expenses/info/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ExpenseCreateComponent,
    data: {mode: ExpenseCreateEditMode.info}
  },
  {
    path: 'expenses/edit/:id',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ExpenseCreateComponent,
    data: {mode: ExpenseCreateEditMode.edit}
  },
  {
    path: 'payments',
    canActivate: mapToCanActivate([AuthGuard]),
    component: ExpenseCreateComponent,
    data: {mode: ExpenseCreateEditMode.create}
  },
  {path: 'home/:tab', component: HomeComponent, canActivate: mapToCanActivate([AuthGuard])},
  {path: 'home', component: HomeComponent, canActivate: mapToCanActivate([AuthGuard])},
  {path: '**', redirectTo: '/home'},
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
