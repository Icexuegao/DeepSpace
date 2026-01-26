package singularity.ui.dialogs;

import arc.Core;
import arc.func.Cons2;
import arc.scene.actions.Actions;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Strings;
import mindustry.gen.Tex;
import mindustry.ui.Styles;
import mindustry.ui.dialogs.BaseDialog;
import singularity.graphic.SglDrawConst;
import singularity.ui.tables.*;
import singularity.world.distribution.DistributeNetwork;
import singularity.world.distribution.MatrixGrid;
import singularity.world.distribution.buffers.BaseBuffer;
import singularity.world.distribution.request.DistRequestBase;
import universecore.util.NumberStrify;

public class DistNetMonitorDialog extends BaseDialog{
  DistributeNetwork distNetwork;

  Monitor currentMonitor, lastMonitor;

  public Seq<MonitorEntry<?>> monitors = Seq.with(
      //传输
      //上行 {0}/秒 - 下行 {1}/秒
      new MonitorEntry<>(new DistFlowRateMonitor(40), (t, n) -> {
        t.add(SglDrawConst.COLOR_ACCENT + Core.bundle.get("misc.transport"));
        t.row();
        t.add("").update(l -> {
          float totalUpload = 0, totalDownload = 0;
          if(n.netStructValid()){
            for(BaseBuffer<?, ?, ?> buffer: n.getCore().getBuffers().values()){
              totalUpload += Math.max(buffer.putRate(), 0);
              totalDownload += Math.max(buffer.readRate(), 0);
            }
          }
          else totalDownload = totalUpload = -1;

          l.setText(Core.bundle.format("data.ioFlow",
              totalUpload == -1? "--": NumberStrify.toByteFix(totalUpload, 2) + Core.bundle.get("misc.perSecond"),
              totalDownload == -1? "--": NumberStrify.toByteFix(totalDownload, 2) + Core.bundle.get("misc.perSecond")
          ));
        });
      }),

      //存储
      //已用 {0}/{1} - 剩余 {2}（{3}%）
      new MonitorEntry<>(new DistContainerMonitor(40), (t, n) -> {
        t.add(SglDrawConst.COLOR_ACCENT + Core.bundle.get("misc.container"));
        t.row();
        t.add("").update(l -> {
          float[] totalUsed = {0}, totalCapacity = {0};
          if(n.netStructValid()){
            for(MatrixGrid grid: n.getGrids()){
              grid.eachUsed((b, a) -> totalUsed[0] += a*b.unit());
              grid.eachCapacity((b, a) -> totalCapacity[0] += a*b.unit());
            }
          }
          else totalUsed[0] = totalCapacity[0] = -1;

          l.setText(Core.bundle.format("data.container",
              totalUsed[0] == -1? "--": NumberStrify.toByteFixNonUnit(totalUsed[0], 0),
              totalCapacity[0] == -1? "--": NumberStrify.toByteFix(totalCapacity[0], 0),
              totalUsed[0] == -1 || totalCapacity[0] == -1 ? "--": NumberStrify.toByteFix(totalCapacity[0] - totalUsed[0], 2),
              totalUsed[0] == -1 || totalCapacity[0] == -1 ? "--": Strings.autoFixed(totalUsed[0]/totalCapacity[0], 1)
          ));
        });
      }),

      //缓存
      //最高占用 {0} - 最大 {1}
      new MonitorEntry<>(new DistBufferMonitor(40), (t, n) -> {
        t.add(SglDrawConst.COLOR_ACCENT + Core.bundle.get("misc.container"));
        t.row();
        t.add("").update(l -> {
          int totalBuffered = 0, maxBuffered = 0;
          if(n.netStructValid())
          for(BaseBuffer<?, ?, ?> value: n.getCore().getBuffers().values()){
            totalBuffered += value.usedCapacity().intValue();
            maxBuffered += value.capacity;
          }
          else totalBuffered = maxBuffered = -1;

          l.setText(Core.bundle.format("data.buffer",
              totalBuffered == -1? "--": NumberStrify.toByteFix(totalBuffered, 0),
              maxBuffered == -1? "--": NumberStrify.toByteFix(maxBuffered, 0)
          ));
        });
      }),

      //任务队列
      //活动 {0} - 已阻塞 {1} - 休眠 {2}
      new MonitorEntry<>(new DistRequestTaskMonitor(40), (t, n) -> {
        t.add(SglDrawConst.COLOR_ACCENT + Core.bundle.get("misc.container"));
        t.row();
        t.add("").update(l -> {
          int activity = 0, blocked = 0, sleeping = 0;
          if(n.netStructValid()){
            for(DistRequestBase task: n.getCore().getDistCore().getRequestTasks()){
              if(task.isBlocked()) blocked++;
              else if(task.sleeping()) sleeping++;
              else activity++;
            }
          }
          else activity = blocked = sleeping = -1;

          l.setText(Core.bundle.format("data.tasks", activity, blocked, sleeping));
        });
      }),

      //设备
      //拓扑空间已用 {0}/{1}
      new MonitorEntry<>(new DistTopologyMonitor(40), (t, n) -> {
          t.add(SglDrawConst.COLOR_ACCENT + Core.bundle.get("misc.container"));
          t.row();
      })
  );

  public DistNetMonitorDialog(){
    super(Core.bundle.get("dialog.distNetMonitor.title"));

    addCloseButton();
  }

  public void rebuild(){
    clear();

    if(distNetwork == null) return;

    currentMonitor = monitors.first().monitor;

    shown(() -> {
      for(MonitorEntry<?> entry: monitors){
        entry.monitor.startMonit(distNetwork);
      }
    });

    hidden(() -> {
      for(MonitorEntry<?> entry: monitors){
        entry.monitor.endMonit(distNetwork);
      }
    });

    table(Tex.pane, root -> {
      root.defaults().margin(0).pad(0);
      root.table(preview -> {
        preview.defaults().height(90).left();
        for(MonitorEntry<?> monitor: monitors){
          preview.button(b -> {
            b.defaults().growX().left();
            monitor.previewBuilder.get(b, distNetwork);
          }, Styles.underlineb, () -> {
            currentMonitor = monitor.monitor;
          }).update(b -> b.setChecked(currentMonitor == monitor.monitor));
          preview.row();
        }
      }).fillX().growY();
      root.table(Tex.pane).grow().get().table().margin(0).grow().update(t -> {
        if(currentMonitor != lastMonitor){
          t.clearActions();
          t.actions(
              Actions.alpha(0, 0.5f),
              Actions.run(() -> {
                t.clearChildren();
                t.add(currentMonitor);
              }),
              Actions.alpha(1, 0.5f)
          );
          lastMonitor = currentMonitor;
        }
      });
    }).margin(0).padLeft(90).padRight(120);
  }

  public void show(DistributeNetwork distNetwork){
    this.distNetwork = distNetwork;

    rebuild();
  }

  @SuppressWarnings("ClassCanBeRecord")
  public static class MonitorEntry<T extends Monitor>{
    public final T monitor;
    public final Cons2<Table, DistributeNetwork> previewBuilder;

    public MonitorEntry(T monitor, Cons2<Table, DistributeNetwork> previewBuilder){
      this.monitor = monitor;
      this.previewBuilder = previewBuilder;
    }
  }
}
