import { Routes } from '@angular/router';
import { UploadComponent } from './pages/upload/upload';
import { HistoricoComponent } from './pages/historico/historico';
import { DetalheComponent } from './pages/detalhe/detalhe';
import { PedidosComponent } from './pages/pedidos/pedidos';

export const routes: Routes = [
  { path: '', redirectTo: 'upload', pathMatch: 'full' },
  { path: 'upload', component: UploadComponent },
  { path: 'historico', component: HistoricoComponent },
  { path: 'importacoes/:id', component: DetalheComponent },
  { path: 'pedidos', component: PedidosComponent },
  { path: '**', redirectTo: 'upload' }
];
