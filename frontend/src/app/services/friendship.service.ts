import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Globals} from '../global/globals';
import { AcceptFriendRequest, FriendRequest } from '../dtos/friend-request';
import { FriendInfoDto } from '../dtos/friend';

@Injectable({
  providedIn: 'root'
})
export class FriendshipService {

  private friendshipBaseUri: string = this.globals.backendUri + '/friendship';

  constructor(private httpClient: HttpClient, private globals: Globals) {
  }


  sendFriendRequest(friendRequest: FriendRequest): Observable<void> {
    return this.httpClient.post<void>(this.friendshipBaseUri, friendRequest);
  }

  getIncomingFriendRequests(): Observable<string[]> {
    return this.httpClient.get<string[]>(this.friendshipBaseUri + '/friend-requests');
  }

  getOutgoingFriendRequests(): Observable<string[]> {
    return this.httpClient.get<string[]>(this.friendshipBaseUri + '/outgoing-friend-requests');
  }

  getFriends(): Observable<string[]> {
    return this.httpClient.get<string[]>(this.friendshipBaseUri + '/friends');
  }

  getFriendsWithDebtInfos(): Observable<FriendInfoDto[]> {
    return this.httpClient.get<FriendInfoDto[]>(this.friendshipBaseUri + '/friends-with-debt-infos');
  }

  acceptFriendRequest(acceptFriendRequest: AcceptFriendRequest): Observable<void> {
    return this.httpClient.put<void>(this.friendshipBaseUri + '/accept', acceptFriendRequest);
  }

  rejectFriendRequest(senderEmailToReject: string): Observable<void> {
    return this.httpClient.delete<void>(this.friendshipBaseUri + `/reject/${senderEmailToReject}`);
  }

  retractFriendRequest(receiverEmailToRetract: string): Observable<void> {
    return this.httpClient.delete<void>(this.friendshipBaseUri + `/retract/${receiverEmailToRetract}`);
  }
  
}
