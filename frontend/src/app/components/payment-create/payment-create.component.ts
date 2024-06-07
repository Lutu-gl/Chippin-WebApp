import {Component, OnInit} from '@angular/core';
import {GroupService} from "../../services/group.service";
import {UserService} from "../../services/user.service";
import {FriendshipService} from "../../services/friendship.service";
import {ActivatedRoute, Router} from "@angular/router";
import {ToastrService} from "ngx-toastr";
import {UserSelection} from "../../dtos/user";
import {NgForm, NgModel} from "@angular/forms";
import {Observable} from "rxjs";
import {GroupDto} from "../../dtos/group";
import {PaymentService} from "../../services/payment.service";
import {PaymentDto} from "../../dtos/payment";

export enum PaymentCreateEditMode {
  create,
  edit,
  info,
}
@Component({
  selector: 'app-payment-create',
  templateUrl: './payment-create.component.html',
  styleUrl: './payment-create.component.scss'
})
export class PaymentCreateComponent implements OnInit {
  mode: PaymentCreateEditMode = PaymentCreateEditMode.create;
  paymentDeleted: boolean;

  payment: PaymentDto = {
    payerEmail: this.userService.getUserEmail(),
    receiverEmail: this.route.snapshot.params.email,
    amount: this.route.snapshot.params.amount,
    groupId: this.route.snapshot.params.id,
    deleted: false,
    archived: false,
    date: null
  }
  private paymentId: number;



  constructor(
    private service: PaymentService,
    protected userService: UserService,
    private friendshipService: FriendshipService,
    private router: Router,
    protected route: ActivatedRoute,
    private notification: ToastrService,
  ) {
  }



  public get heading(): string {
    switch (this.mode) {
      case PaymentCreateEditMode.create:
        return 'Create New Payment';
      case PaymentCreateEditMode.edit:
        return 'Edit Payment';
      case PaymentCreateEditMode.info:
        return 'Payment Info';
      default:
        return '?';
    }
  }

  public get submitButtonText(): string {
    switch (this.mode) {
      case PaymentCreateEditMode.create:
        return 'Create';
      case PaymentCreateEditMode.edit:
        return 'Edit';
      case PaymentCreateEditMode.info:
        return 'Edit this Payment';
      default:
        return '?';
    }
  }

  private get modeActionFinished(): string {
    switch (this.mode) {
      case PaymentCreateEditMode.create:
        return 'made';
      case PaymentCreateEditMode.edit:
        return 'edited';
      default:
        return '?';
    }
  }

  ngOnInit(): void {
    this.route.data.subscribe(data => {
      this.mode = data.mode;
    });

    if (this.mode != PaymentCreateEditMode.create) {
      this.getPayment();
    } else {
      let emailString = this.userService.getUserEmail();
      if(emailString === null) {
        this.notification.error(`You need to be logged in to create a group. Please logout and login again.`);
        return;
      }
      this.paymentDeleted = false;
      this.payment.deleted = false;
    }
  }

  public dynamicCssClassesForInput(input: NgModel): any {
    return {
      'is-invalid': !input.valid && !input.pristine,
    };
  }

  public onSubmit(form: NgForm): void {
    if (this.mode == PaymentCreateEditMode.info) {
      //this.router.navigate([`groups/${(this.route.snapshot.paramMap.get('id'))}/edit`]);
      return;
    }
    if (form.valid) {
      let observable: Observable<PaymentDto>;
      switch (this.mode) {
        case PaymentCreateEditMode.create:
          observable = this.service.createPayment(this.payment);
          break;
        case PaymentCreateEditMode.edit:
          observable = this.service.updatePayment(Number(this.route.snapshot.paramMap.get('paymentId')), this.payment);
          break;
        default:
          console.error('Unknown PaymentCreateEditMode', this.mode);
          return;
      }
      observable.subscribe({
        next: data => {
          this.notification.success(`Payment successfully ${this.modeActionFinished}.`);
          const paymentId = data.id || this.payment.id;
          this.router.navigate(['/group', this.payment.groupId]);
        },
        error: error => {
          if (error && error.error && error.error.errors) {
            //this.notification.error(`${error.error.errors.join('. \n')}`);
            for (let i = 0; i < error.error.errors.length; i++) {
              this.notification.error(`${error.error.errors[i]}`);
            }
          } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
            this.notification.error(`${error.error.message}`);
          } else if (error && error.error.detail) {
            this.notification.error(`${error.error.detail}`);
          } else {
            switch (this.mode) {
              case PaymentCreateEditMode.create:
                console.error('Error making payment', error);
                this.notification.error(`Creation of payment did not work!`);
                break;
              case PaymentCreateEditMode.edit:
                console.error('Error editing payment', error);
                this.notification.error(`Edit of payment did not work!`);
                break;
              default:
                console.error('Unknown PaymentCreateEditMode. Operation did not work!', this.mode);
            }
          }
        }
      });
    }
  }

  goBack() {
    this.router.navigate([`/group/${this.route.snapshot.paramMap.get('id')}`]);
  }

  protected readonly PaymentCreateEditMode = PaymentCreateEditMode;

  private getPayment() {
    const paymentId = Number(this.route.snapshot.paramMap.get('paymentId'));
    this.paymentId = paymentId;
    if(paymentId) {
      this.service.getPaymentById(paymentId).subscribe({
        next: data => {
          console.log(data)
          this.payment = data;
          this.paymentDeleted = data.deleted;
        },
        error: error => {
          if (error && error.error && error.error.errors) {
            //this.notification.error(`${error.error.errors.join('. \n')}`);
            for (let i = 0; i < error.error.errors.length; i++) {
              this.notification.error(`${error.error.errors[i]}`);
            }
          } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
            this.notification.error(`${error.error.message}`);
          } else if (error && error.error.detail) {
            this.notification.error(`${error.error.detail}`);
          } else {
            switch (this.mode) {
              case PaymentCreateEditMode.create:
                console.error('Error making payment', error);
                this.notification.error(`Creation of payment did not work!`);
                break;
              case PaymentCreateEditMode.edit:
                console.error('Error editing payment', error);
                this.notification.error(`Edit of payment did not work!`);
                break;
              case PaymentCreateEditMode.info:
                console.error('Error getting payment', error);
                this.notification.error(`Getting payment did not work!`);
                break;
              default:
                console.error('Unknown PaymentCreateEditMode. Operation did not work!', this.mode);
            }
          }
        }
      })
    }
  }

  paymentIsDeleted(): boolean {
    return this.paymentDeleted;
  }

  modeIsInfo() {
    return this.mode === PaymentCreateEditMode.info;
  }

  modeIsCreate(): boolean {
    return this.mode === PaymentCreateEditMode.create;
  }

  modeIsEdit(): boolean {
    return this.mode === PaymentCreateEditMode.edit;
  }

  public editExistingPayment(paymentId: number, payment: PaymentDto) {
    this.service.updatePayment(paymentId, payment).subscribe({
      next: data => {
        this.notification.success(`Payment successfully edited.`);
        this.payment = data;
        this.goBack();
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          //this.notification.error(`${error.error.errors.join('. \n')}`);
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else if (error && error.error.detail) {
          this.notification.error(`${error.error.detail}`);
        } else {
          console.error('Error editing payment', error);
          this.notification.error("Could not edit payment!");
        }
      }
    })
  }

  public deleteExistingPayment() {
    this.service.deletePayment(this.paymentId).subscribe({
      next: data => {
        this.notification.success(`Payment successfully deleted.`);
        this.paymentDeleted = true;
        this.goBack();
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          //this.notification.error(`${error.error.errors.join('. \n')}`);
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else if (error && error.error.detail) {
          this.notification.error(`${error.error.detail}`);
        } else {
          console.error('Error deleting payment', error);
          this.notification.error("Could not delete payment!");
        }
      }
    })
  }

  public recoverDeletedPayment() {
    this.service.recoverPayment(this.paymentId).subscribe({
      next: data => {
        this.notification.success(`Payment successfully recovered.`);
        this.paymentDeleted = false;
        this.payment = data;
      },
      error: error => {
        if (error && error.error && error.error.errors) {
          //this.notification.error(`${error.error.errors.join('. \n')}`);
          for (let i = 0; i < error.error.errors.length; i++) {
            this.notification.error(`${error.error.errors[i]}`);
          }
        } else if (error && error.error && error.error.message) { // if no detailed error explanation exists. Give a more general one if available.
          this.notification.error(`${error.error.message}`);
        } else if (error && error.error.detail) {
          this.notification.error(`${error.error.detail}`);
        } else {
          console.error('Error recovering payment', error);
          this.notification.error("Could not recover payment!");
        }
      }
    })
  }

  isUserInvolvedInPayment(): boolean {
    const user = this.userService.getUserEmail();
    return this.payment && (this.payment.payerEmail === user || this.payment.receiverEmail === user);
  }
}
