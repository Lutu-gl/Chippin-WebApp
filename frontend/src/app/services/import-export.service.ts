import { Injectable } from '@angular/core';
import { Globals } from '../global/globals';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ImportDto } from '../dtos/importExport';

@Injectable({
  providedIn: 'root'
})
export class ImportExportService {

  private importBaseUri: string = this.globals.backendUri + '/import';
  private exportBaseUri: string = this.globals.backendUri + '/export';

  constructor(
    private httpClient: HttpClient,
    private globals: Globals
  ) { }

  importData(importDto: ImportDto): Observable<void> {
    return this.httpClient.post<void>(this.importBaseUri + '/splitwise', importDto);
  }

  exportData(groupId: number): Observable<Blob> {
    return this.httpClient.get(this.exportBaseUri + '/' + groupId, { responseType: 'blob' });
  }

}
