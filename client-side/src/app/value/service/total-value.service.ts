import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { TotalBalance } from 'src/app/balance/model/total-balance';
import { TotalValues } from '../model/total-values';

@Injectable({
  providedIn: 'root'
})
export class TotalValueService {

  private totalBalances: TotalBalance[] = [];

  private totalValueSubject = new BehaviorSubject<TotalValues>({
    totalValue: 0,
    totalValue1h: 0,
    totalValue24h: 0,
    totalValue7d: 0,
    percentageChange1h: 0,
    percentageChange24h: 0,
    percentageChange7d: 0
  });

  public currentTotalValue$ = this.totalValueSubject.asObservable();

  constructor() { }

  setTotalValue(totalBalance: TotalBalance) {
    this.totalBalances.push(totalBalance);

    if (this.totalBalances.length === 3) {

      let totalValue = 0;
      let totalValue1h = 0;
      let totalValue24h = 0;
      let totalValue7d = 0;

      this.totalBalances.forEach(element => {
        totalValue += element.totalBalance;
        totalValue1h += element.totalBalance1h;
        totalValue24h += element.totalBalance24h;
        totalValue7d += element.totalBalance7d;
      });

      let resultTotalValue1h = totalValue - totalValue1h;
      let resultTotalValue24h = totalValue - totalValue24h;
      let resultTotalValue7d = totalValue - totalValue7d;

      let resultPercent1h = this.calculatePercentageChange(totalValue, totalValue1h);
      let resultPercent24h = this.calculatePercentageChange(totalValue, totalValue24h);
      let resultPercent7d = this.calculatePercentageChange(totalValue, totalValue7d);

      const totalValues: TotalValues = {
        totalValue: totalValue,
        totalValue1h: resultTotalValue1h,
        totalValue24h: resultTotalValue24h,
        totalValue7d: resultTotalValue7d,
        percentageChange1h: resultPercent1h,
        percentageChange24h: resultPercent24h,
        percentageChange7d: resultPercent7d
      };
      this.totalValueSubject.next(totalValues);

      this.totalBalances = [];
    }
  }

  calculatePercentageChange(value1: number, value2: number): number {
    if (value1 === 0 && value2 === 0) {
      return 0.00;
    }
    const percentageChange = ((value1 - value2) / value1) * 100;
    return percentageChange;
  }
}