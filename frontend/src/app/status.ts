import { StatusImportacao } from './models';

const ROTULOS: Record<StatusImportacao, string> = {
  RECEBIDA: 'Recebida',
  PROCESSANDO: 'Processando',
  CONCLUIDA: 'Concluida',
  CONCLUIDA_COM_ERROS: 'Concluida com erros',
  FALHOU: 'Falhou'
};

export function rotuloStatus(status: StatusImportacao): string {
  return ROTULOS[status] ?? status;
}

export function classeStatus(status: StatusImportacao): string {
  return `badge badge-${status.toLowerCase()}`;
}
