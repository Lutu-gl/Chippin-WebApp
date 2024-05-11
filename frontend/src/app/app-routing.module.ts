import {NgModule} from '@angular/core';
import {mapToCanActivate, RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './components/home/home.component';
import {LoginComponent} from './components/login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {PantryComponent} from "./components/pantry/pantry.component";
import {RegisterComponent} from "./components/register/register.component";
import {GroupListComponent} from "./components/group-list/group-list.component";
import {GroupCreateComponent, GroupCreateEditMode} from "./components/group-list/group-create/group-create.component";


const routes: Routes = [
  {path: '', component: HomeComponent},
  {path: 'login', component: LoginComponent},
  {path: 'groups', canActivate: mapToCanActivate([AuthGuard]), component: GroupListComponent},
  {path: 'groups/:id/edit', canActivate: mapToCanActivate([AuthGuard]), component: GroupCreateComponent, data: {mode: GroupCreateEditMode.edit}},
  {path: 'groups/create', canActivate: mapToCanActivate([AuthGuard]), component: GroupCreateComponent, data: {mode: GroupCreateEditMode.create}},
  {path: 'group/:id', canActivate:mapToCanActivate([AuthGuard]), children: [
      {path: 'pantry', component: PantryComponent}
    ]},
  {path: 'register', component: RegisterComponent},
  {path: '**', redirectTo: ''}
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {useHash: true})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
