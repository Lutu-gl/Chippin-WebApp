import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { Observable } from 'rxjs';
import { GeneralInformationDto } from '../dtos/generalInformation';

@Injectable({
  providedIn: 'root'
})
export class GeneralInformationService {

  private generalInformationBaseUri: string = this.globals.backendUri + '/general-information';

  constructor(private httpClient: HttpClient, private globals: Globals) { }

  getGeneralInformation(): Observable<GeneralInformationDto> {
    return this.httpClient.get<GeneralInformationDto>(this.generalInformationBaseUri, {responseType: 'json'});
  }

}
