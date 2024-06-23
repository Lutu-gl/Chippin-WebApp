import { Component, OnInit } from '@angular/core';
import { GeneralInformationDto } from 'src/app/dtos/generalInformation';
import { AuthService } from 'src/app/services/auth.service';
import { GeneralInformationService } from 'src/app/services/general-information.service';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent implements OnInit {

  constructor(public authService: AuthService, private generalInformationService: GeneralInformationService) { }

  generalInformation: GeneralInformationDto = null;

  ngOnInit(): void {
    this.generalInformationService.getGeneralInformation().subscribe(generalInformation => {
      this.generalInformation = generalInformation;
    });
  }

}
