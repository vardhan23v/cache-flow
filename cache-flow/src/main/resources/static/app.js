const inr = v => v == null ? '—' : '₹' + Math.round(v).toLocaleString('en-IN');
const esc = s => String(s).replace(/[&<>"]/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;'}[c]));
const tbody = document.getElementById('rows');
const search = document.getElementById('search');
const category = document.getElementById('category');
let previous = {};

function applyFilter() {
  const q = search.value.toLowerCase(), c = category.value;
  for (const tr of tbody.rows) {
    const ok = (!c || tr.dataset.category === c) && tr.cells[0].textContent.toLowerCase().includes(q);
    tr.style.display = ok ? '' : 'none';
  }
}

function render(data) {
  document.getElementById('lastSync').textContent =
    data.lastSync && data.lastSync !== 'null' ? new Date(data.lastSync).toLocaleString('en-GB') : 'never';
  tbody.innerHTML = data.rows.map(r => {
    const key = r.storeA + '|' + r.storeB;
    const changed = previous[r.sku] !== undefined && previous[r.sku] !== key;
    previous[r.sku] = key;
    const diff = r.difference == null ? '—' : `${inr(r.difference)} (${r.differencePct}%)`;
    return `<tr data-sku="${esc(r.sku)}" data-category="${esc(r.category)}" class="${changed ? 'flash' : ''}">
      <td><a href="/product/${encodeURIComponent(r.sku)}">${esc(r.name)}</a></td><td>${esc(r.category)}</td>
      <td class="num ${r.best === 'Store A' ? 'cheapest' : ''}">${inr(r.storeA)}</td>
      <td class="num ${r.best === 'Store B' ? 'cheapest' : ''}">${inr(r.storeB)}</td>
      <td class="num">${diff}</td>
      <td><span class="badge ${r.best === 'Same Price' ? 'tie' : ''}">${esc(r.best)}</span></td></tr>`;
  }).join('');
  applyFilter();
}

async function refresh() {
  try { render(await (await fetch('/api/comparison')).json()); } catch (e) { console.warn(e); }
}

document.getElementById('syncForm').addEventListener('submit', async e => {
  e.preventDefault();
  const btn = e.target.querySelector('button'); btn.disabled = true; btn.textContent = 'Syncing…';
  await fetch('/api/sync', {method: 'POST'});
  await refresh();
  btn.disabled = false; btn.textContent = '⟳ Sync / Refresh Prices Now';
});
search.addEventListener('input', applyFilter);
category.addEventListener('change', applyFilter);
refresh();
setInterval(refresh, 5000);
