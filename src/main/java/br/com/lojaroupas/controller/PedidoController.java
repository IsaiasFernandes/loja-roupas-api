package br.com.lojaroupas.controller;

import br.com.lojaroupas.model.Pedido;
import br.com.lojaroupas.model.Produto;
import br.com.lojaroupas.repository.ClienteRepository;
import br.com.lojaroupas.repository.PedidoRepository;
import br.com.lojaroupas.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository repository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Pedido pedido) {
        String erro = validar(pedido);
        if (erro != null) {
            return ResponseEntity.badRequest().body(erro);
        }
        pedido.setId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(repository.save(pedido));
    }

    @GetMapping
    public List<Pedido> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable Long id, @RequestBody Pedido dados) {
        Optional<Pedido> existente = repository.findById(id);
        if (existente.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        String erro = validar(dados);
        if (erro != null) {
            return ResponseEntity.badRequest().body(erro);
        }
        Pedido pedido = existente.get();
        pedido.setClienteId(dados.getClienteId());
        pedido.setProdutoId(dados.getProdutoId());
        pedido.setQuantidade(dados.getQuantidade());
        return ResponseEntity.ok(repository.save(pedido));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /** Regras simples do pedido. Retorna a mensagem de erro, ou null se estiver tudo certo. */
    private String validar(Pedido pedido) {
        if (pedido.getClienteId() == null || !clienteRepository.existsById(pedido.getClienteId())) {
            return "Cliente não encontrado";
        }
        Optional<Produto> produto = pedido.getProdutoId() == null
                ? Optional.empty()
                : produtoRepository.findById(pedido.getProdutoId());
        if (produto.isEmpty()) {
            return "Produto não encontrado";
        }
        if (!Boolean.TRUE.equals(produto.get().getEstoque())) {
            return "Produto sem estoque";
        }
        if (pedido.getQuantidade() == null || pedido.getQuantidade() <= 0) {
            return "Quantidade deve ser maior que zero";
        }
        return null;
    }
}
