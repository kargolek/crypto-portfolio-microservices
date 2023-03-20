import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http'
import { BrowserModule } from '@angular/platform-browser';
import { ToastContainerModule } from 'ngx-toastr';
import { ToastrModule } from 'ngx-toastr';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { SatPopoverModule } from '@ncstate/sat-popover';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ToastMessageService } from './core/service/toast-message/toast-message.service';
import { InputWalletsComponent } from './wallet/input-wallets/input-wallets.component';
import { ToastrService } from 'ngx-toastr';
import { BalanceTableComponent } from './balance/component/balance-table/balance-table.component';
import { BalanceService } from './balance/service/balance.service';
import { ErrorHandlerService } from './balance/service/error-handler.service';
import { NavbarComponent } from './core/component/navbar/navbar.component';
import { LoadingComponent } from './core/component/loading/loading.component';
import { LoadingInterceptor } from './core/interceptor/loading/loading.interceptor';
import { PriceService } from './price/service/price.service';
import { PriceBarComponent } from './price/component/price-bar/price-bar.component';
import { CryptoIconComponent } from './shared/component/crypto-icon-text/crypto-icon.component';
import { EthereumBalanceComponent } from './balance/component/ethereum-balance/ethereum-balance.component';
import { PolygonBalanceComponent } from './balance/component/polygon-balance/polygon-balance.component';
import { AbstractBalanceComponent } from './balance/component/abstract-balance/abstract-balance.component';

@NgModule({
  declarations: [
    AppComponent,
    InputWalletsComponent,
    BalanceTableComponent,
    NavbarComponent,
    LoadingComponent,
    PriceBarComponent,
    CryptoIconComponent,
    EthereumBalanceComponent,
    PolygonBalanceComponent,
    AbstractBalanceComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    ToastContainerModule,
    BrowserAnimationsModule,
    MatListModule,
    MatToolbarModule,
    MatIconModule,
    SatPopoverModule
  ],
  providers: [
    ToastMessageService,
    ToastrService,
    BalanceService,
    ErrorHandlerService,
    PriceService,
    { provide: HTTP_INTERCEPTORS, useClass: LoadingInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }