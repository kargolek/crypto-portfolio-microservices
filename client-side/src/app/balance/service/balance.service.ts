import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap } from 'rxjs';
import { InputWalletsDataService } from 'src/app/shared/services/input-wallets-data.service';
import { UserWallet } from '../model/user-wallet';
import { ErrorHandlerService } from './error-handler.service';

@Injectable({
  providedIn: 'root'
})
export class BalanceService {

  private dataSubject = new BehaviorSubject<UserWallet>({ name: '', symbol: '', total: { totalQuantity: 0, totalBalance: 0 }, balance: [] });
  data$ = this.dataSubject.asObservable();

  constructor(
    private http: HttpClient,
    private errorHandler: ErrorHandlerService,
    private inputWalletsData: InputWalletsDataService
  ) { }

  private getWalletBalancesURL = 'http://localhost:8080/api/v1/wallet/eth/balance?wallets=';

  getWalletsBalance(): Observable<UserWallet> {
    const wallets = this.inputWalletsData.getDataFromSessionStorage();
    const data = this.http.get<UserWallet>(this.getWalletBalancesURL + wallets).pipe(
      tap(data => {
        data.balance.forEach(wallet => {
          wallet.walletAddress = this.trimAddress(wallet.walletAddress);
        });
        this.dataSubject.next(data);
      }),
      catchError((error: HttpErrorResponse) => this.errorHandler.handleHttpError(error))
    );
    return data;
  }

  trimAddress(address: string): string {
    return address.slice(0, 10);
  }

}
