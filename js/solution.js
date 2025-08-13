function solutionPatch() {
  const nodes = document.querySelectorAll(".solution");
  nodes.forEach(node => {
    const summary = document.createElement("summary");
    const header = document.createElement("h5");
    header.appendChild(document.createTextNode("Solution"))
    summary.appendChild(header);

    const details = document.createElement("details");
    const children = Array.from(node.children);
    children.unshift(summary);
    details.replaceChildren(...children)

    node.replaceChildren(details);
  })
}
addEventListener('load', () => solutionPatch());
