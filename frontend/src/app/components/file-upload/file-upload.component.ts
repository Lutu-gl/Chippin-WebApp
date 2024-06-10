import { Component } from '@angular/core';

@Component({
  selector: 'app-file-upload',
  templateUrl: './file-upload.component.html',
  styleUrls: ['./file-upload.component.scss']
})
export class FileUploadComponent {



  onBasicUploadAuto(event: any): void {
    console.log("Event getriggerted")
    console.log('File uploaded:', event.files[0]);
  }
}
