import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InputWalletsComponent } from './wallet/input-wallets/input-wallets.component';

const routes: Routes = [
  { path: '', redirectTo: 'check', pathMatch: 'full' },
  { path: 'check', component: InputWalletsComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
