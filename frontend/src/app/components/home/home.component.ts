import {Component, OnInit} from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AuthService } from 'src/app/services/auth.service';
import {ActivatedRoute, Router} from "@angular/router";


@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  tabMenuItems: MenuItem[] | undefined;
  tabMenuActiveItem: MenuItem | undefined;

  constructor(
    public authService: AuthService,
    private route: ActivatedRoute,
    private router: Router
){
  }

  ngOnInit(): void {
    this.tabMenuItems = [
      { label: 'My friends', icon: 'pi pi-fw pi-users' },
      { label: 'My groups', icon: 'pi pi-fw pi-th-large' },
      { label: 'Shopping lists', icon: 'pi pi-fw pi-shopping-cart' }
    ];

    this.route.paramMap.subscribe(params => {
      const tab = params.get('tab');
      console.log(params)
      switch (tab) {
        case 'friends':
          this.tabMenuActiveItem = this.tabMenuItems[0];
          break;
        case 'groups':
          this.tabMenuActiveItem = this.tabMenuItems[1];
          break;
        case 'shopping-lists':
          this.tabMenuActiveItem = this.tabMenuItems[2];
          break;
        default:
          this.tabMenuActiveItem = this.tabMenuItems[0];
      }
    });
  }

  onActiveItemChange(event: MenuItem) {
    // Change route to match the selected tab
    switch (event.label) {
      case 'My friends':
        this.router.navigate(['home','friends']);
        break;
      case 'My groups':
        this.router.navigate(['home', 'groups']);
        break;
      case 'Shopping lists':
        this.router.navigate(['home', 'shopping-lists']);
        break;
    }

    this.tabMenuActiveItem = event;
  }

  isFriendsSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[0];
  }

  isGroupsSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[1];
  }

  isShoppingListsSelected(): boolean {
    return this.tabMenuActiveItem === this.tabMenuItems[2];
  }


}


// import {Component, OnInit} from '@angular/core';
// import {AuthService} from '../../services/auth.service';
// import {GroupListDto} from "../../dtos/group";
// import {GroupService} from "../../services/group.service";
// import {ToastrService} from "ngx-toastr";
// import {ItemListDetailDto} from "../../dtos/itemlist";
// import {RecipeDetailDto, RecipeListDto} from "../../dtos/recipe";
// import { FriendshipService } from 'src/app/services/friendship.service';
// import { AcceptFriendRequest } from 'src/app/dtos/friend-request';
// import {RecipeService} from "../../services/recipe.service";

// @Component({
//   selector: 'app-home',
//   templateUrl: './home.component.html',
//   styleUrls: ['./home.component.scss']
// })
// export class HomeComponent implements OnInit {

//   constructor(
//     public authService: AuthService,
//     private groupService: GroupService,
//     private friendshipService: FriendshipService,
//     private recipeService: RecipeService,
//     private notification: ToastrService,
//   ) { }
//   groups: GroupListDto[] = [];
//   incomingFriendRequests: string[] = [];
//   friends: string[] = [];
//   recipes: RecipeListDto[] = [];

//   ngOnInit(): void {
//     console.log("logged in? ", this.authService.isLoggedIn());
//     if(this.authService.isLoggedIn()){
//       this.friendshipService.getIncomingFriendRequests().subscribe({
//         next: data => {
//           this.incomingFriendRequests = data;
//         },
//         error: error => {
//           this.printError(error);
//         }
//       });

//       this.friendshipService.getFriends().subscribe({
//         next: data => {
//           this.friends = data;
//         },
//         error: error => {
//           this.printError(error);
//         }
//       });

//       this.groupService.getGroups().subscribe({
//         next: data => {
//           this.groups = data;
//         },
//         error: error => {
//           this.printError(error);
//         }
//       });
//       this.recipeService.getRecipesFromUser().subscribe({
//         next: data => {
//           this.recipes = data;
//         },
//         error: error => {
//           this.printError(error);
//         }
//       });


//     }
//   }

//   printError(error): void {
//     if (error && error.error && error.error.errors) {
//       for (let i = 0; i < error.error.errors.length; i++) {
//         this.notification.error(`${error.error.errors[i]}`);
//       }
//     } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
//       this.notification.error(`${error.error.message}`);
//     } else {
//       console.error('Error', error);
//       if(error.status !== 401) {
//         const errorMessage = error.status === 0
//           ? 'Is the backend up?'
//           : error.message.message;
//         this.notification.error(errorMessage, 'Could not connect to the server.');
//       }
//     }
//   }

// }
