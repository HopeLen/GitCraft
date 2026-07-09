# Graph Report - GitCraft  (2026-07-08)

## Corpus Check
- 25 files · ~4,876 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 155 nodes · 265 edges · 24 communities (15 shown, 9 thin omitted)
- Extraction: 89% EXTRACTED · 11% INFERRED · 0% AMBIGUOUS · INFERRED: 28 edges (avg confidence: 0.81)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `4ddfb606`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- [[_COMMUNITY_Repo JSON Persistence|Repo JSON Persistence]]
- [[_COMMUNITY_Mod Entry & Commands|Mod Entry & Commands]]
- [[_COMMUNITY_Init Command Logic|Init Command Logic]]
- [[_COMMUNITY_Project Docs & Tooling|Project Docs & Tooling]]
- [[_COMMUNITY_Place Command Logic|Place Command Logic]]
- [[_COMMUNITY_Repo Name Suggestions|Repo Name Suggestions]]
- [[_COMMUNITY_Mixin Scaffolding|Mixin Scaffolding]]
- [[_COMMUNITY_Data Generation|Data Generation]]
- [[_COMMUNITY_Mod Icon & Identity|Mod Icon & Identity]]
- [[_COMMUNITY_.execute|.execute]]
- [[_COMMUNITY_CommitData|CommitData]]
- [[_COMMUNITY_ObjectStore|ObjectStore]]
- [[_COMMUNITY_.capture|.capture]]
- [[_COMMUNITY_CLAUDE|CLAUDE.md]]
- [[_COMMUNITY_GRAPH_REPORT.md Architecture Review|GRAPH_REPORT.md Architecture Review]]
- [[_COMMUNITY_Graphify AST-Only Update|Graphify AST-Only Update]]
- [[_COMMUNITY_Graphify Knowledge Graph|Graphify Knowledge Graph]]
- [[_COMMUNITY_Graphify QueryPathExplain Commands|Graphify Query/Path/Explain Commands]]
- [[_COMMUNITY_Graphify Wiki Index Navigation|Graphify Wiki Index Navigation]]
- [[_COMMUNITY_CC0 License|CC0 License]]
- [[_COMMUNITY_Fabric Documentation Setup Guide|Fabric Documentation Setup Guide]]
- [[_COMMUNITY_PlacementRenderer.java|PlacementRenderer.java]]

## God Nodes (most connected - your core abstractions)
1. `Placement` - 10 edges
2. `RepoData` - 10 edges
3. `PlacementRenderer` - 9 edges
4. `Place` - 6 edges
5. `GitCraft` - 5 edges
6. `CommitData` - 5 edges
7. `Located` - 5 edges
8. `Region` - 5 edges
9. `Commits` - 4 edges
10. `Init` - 4 edges

## Surprising Connections (you probably didn't know these)
- `Located` --references--> `Placement`  [EXTRACTED]
  src/main/java/net/hopelen/gitcraft/logic/PlacementLocator.java → src/main/java/net/hopelen/gitcraft/logic/RepoJson.java
- `Located` --references--> `RepoData`  [EXTRACTED]
  src/main/java/net/hopelen/gitcraft/logic/PlacementLocator.java → src/main/java/net/hopelen/gitcraft/logic/RepoJson.java

## Import Cycles
- None detected.

## Hyperedges (group relationships)
- **Graphify Codebase Question Workflow** — claude_graphify_knowledge_graph, claude_graphify_query_commands, claude_graphify_wiki_navigation, claude_graph_report, claude_graphify_ast_update [EXTRACTED 1.00]

## Communities (24 total, 9 thin omitted)

### Community 0 - "Repo JSON Persistence"
Cohesion: 0.21
Nodes (12): String, Located, BlockPosData, BlockPos, Gson, List, Path, String (+4 more)

### Community 1 - "Mod Entry & Commands"
Cohesion: 0.27
Nodes (6): Logger, ModInitializer, ModCommands, GitCraft, Override, String

### Community 2 - "Init Command Logic"
Cohesion: 0.24
Nodes (7): Init, BlockPos, FabricClientCommandSource, Path, String, FabricClientCommandSource, Unplace

### Community 3 - "Project Docs & Tooling"
Cohesion: 0.50
Nodes (3): GitCraft, License, Setup

### Community 4 - "Place Command Logic"
Cohesion: 0.17
Nodes (10): Checkout, Path, String, BlockPos, FabricClientCommandSource, String, Place, BlockPos (+2 more)

### Community 5 - "Repo Name Suggestions"
Cohesion: 0.43
Nodes (5): FabricClientCommandSource, List, String, Suggestions, SuggestionProvider

### Community 6 - "Mixin Scaffolding"
Cohesion: 0.53
Nodes (4): CallbackInfo, Inject, Mixin, ExampleMixin

### Community 7 - "Data Generation"
Cohesion: 0.47
Nodes (4): DataGeneratorEntrypoint, FabricDataGenerator, GitCraftDataGenerator, Override

### Community 11 - ".execute"
Cohesion: 0.27
Nodes (7): Commit, FabricClientCommandSource, Path, String, Path, String, Refs

### Community 12 - "CommitData"
Cohesion: 0.44
Nodes (5): CommitData, Commits, Gson, Path, String

### Community 13 - "ObjectStore"
Cohesion: 0.48
Nodes (3): Path, String, ObjectStore

### Community 14 - ".capture"
Cohesion: 0.47
Nodes (4): Level, BlockPos, String, Snapshot

### Community 24 - "PlacementRenderer.java"
Cohesion: 0.20
Nodes (16): CameraRenderState, Color4f, Frustum, GpuBufferSlice, IRenderer, Matrix4fc, ProfilerFiller, RenderBuffers (+8 more)

## Knowledge Gaps
- **12 isolated node(s):** `graphify`, `Setup`, `License`, `Graphify Knowledge Graph`, `Graphify Query/Path/Explain Commands` (+7 more)
  These have ≤1 connection - possible missing edges or undocumented components.
- **9 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Placement` connect `Repo JSON Persistence` to `.execute`, `Place Command Logic`?**
  _High betweenness centrality (0.097) - this node is a cross-community bridge._
- **Why does `RepoData` connect `Repo JSON Persistence` to `.execute`?**
  _High betweenness centrality (0.066) - this node is a cross-community bridge._
- **Why does `PlacementRenderer` connect `PlacementRenderer.java` to `Mod Entry & Commands`, `Init Command Logic`?**
  _High betweenness centrality (0.047) - this node is a cross-community bridge._
- **What connects `graphify`, `Setup`, `License` to the rest of the system?**
  _12 weakly-connected nodes found - possible documentation gaps or missing edges._