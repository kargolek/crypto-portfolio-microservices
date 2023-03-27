import { Component } from '@angular/core';
import { BalanceService } from '../../service/balance.service';
import { AbstractBalanceComponent } from '../abstract-balance/abstract-balance.component';

@Component({
  selector: 'app-avalanche-balance',
  providers: [BalanceService],
  templateUrl: '../abstract-balance/abstract-balance.component.html',
  styleUrls: ['../abstract-balance/abstract-balance.component.scss']
})
export class AvalancheBalanceComponent extends AbstractBalanceComponent {

    constructor(balanceService: BalanceService) {
        super(balanceService);
    }

    override url: string = this.balanceService.getAvalancheWalletBalancesURL;

    override setTokenName(): string {
        return "Avalanche"
    }
}
