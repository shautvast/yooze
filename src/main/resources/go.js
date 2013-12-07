function init(){
	var $ = go.GraphObject.make;  // for conciseness in defining templates
    var diagram = $(go.Diagram, "myDiagram");  // 


    // Define a node template showing class names.
    // Double-clicking opens up the documentation for that class.
    diagram.nodeTemplate =
      $(go.Node, "Auto",
        { doubleClick: nodeDoubleClick },  // this function is defined below
        { toolTip:
            $(go.Adornment, "Auto",
              $(go.Shape, { fill: "lightyellow" }),
              $(go.TextBlock, "double-click\nfor documentation",
                { margin: 5 })
            )
        },
        $(go.Shape, { fill: "darkslategray", stroke: null }),
        $(go.TextBlock,
          { font: "bold 13px Helvetica, bold Arial, sans-serif",
            stroke: "white", margin: 3 },
          new go.Binding("text", "key")));

    // Define a trivial link template with no arrowhead
    diagram.linkTemplate =
      $(go.Link,  // the whole link panel
        { selectable: false },
        $(go.Shape));  // the link shape, with the default black stroke

    // Collect all of the data for the model of the class hierarchy
    var nodeDataArray = [];

    // Iterate over all of the classes in "go"
    for (k in go) {
      var cls = go[k];
      if (!cls) continue;
      var proto = cls.prototype;
      if (!proto) continue;
      proto.constructor.className = k;  // remember name
      // find base class constructor
      var base = Object.getPrototypeOf(cls.prototype).constructor;
      if (base === Object) {  // "root" node?
        nodeDataArray.push({ key: k });
      } else {
        // add a node for this class and a tree-parent reference to the base class name
        nodeDataArray.push({ key: k, parent: base.className });
      }
    }

    // Create the model for the hierarchy diagram
    diagram.model = new go.TreeModel(nodeDataArray);

    // Now collect all node data that are singletons
    var singlesArray = [];  // for classes that don't inherit from another class
    var it = diagram.nodes;
    while (it.next()) {
      var node = it.value;
      if (node.linksConnected.count === 0) {
        singlesArray.push(node.data);
      }
    }

    // Remove the unconnected class nodes from the main Diagram
    for (var i = 0; i < singlesArray.length; i++) {
      diagram.model.removeNodeData(singlesArray[i]);
    }

    // Now lay out the diagram as a tree;
    // separate trees are arranged vertically above each other.
    diagram.layout = $(go.TreeLayout, { nodeSpacing: 3 });

    // Display the unconnected classes in a separate Diagram
    var singletons =
      $(go.Diagram, "mySingletons",
        { nodeTemplate: diagram.nodeTemplate, // Share the node template with the main Diagram
          layout:
            $(go.GridLayout,
              { wrappingColumn: 1,  // Put the unconnected nodes in a column
                spacing: new go.Size(3, 3) }),
          model: new go.Model(singlesArray) });  // Use a separate model
  }

  // When a Node is double clicked, open the documentation for the corresponding class in a new window
  function nodeDoubleClick(event, node) {
    window.open("../api/symbols/" + node.data.key + ".html");
  }
