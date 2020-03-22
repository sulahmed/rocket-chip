// See LICENSE.SiFive for license details.

package freechips.rocketchip.monitors

import chisel3.aop.injecting._

import chisel3.experimental.{BaseModule, FixedPoint}
import chisel3.{RawModule, VecInit}

import chisel3.aop.injecting.InjectingAspect
import chisel3.aop._
import chisel3._
import firrtl.options.TargetDirAnnotation
import freechips.rocketchip.rocket._
import freechips.rocketchip.stage.{ConfigsAnnotation, TopModuleAnnotation}
import freechips.rocketchip.system.{RocketChipStage, TestHarness}
import chisel3.internal.firrtl._
//import freechips.rocketchip.util.HasRocketChipStageUtils
import freechips.rocketchip.util.SelectDiplomacy
import freechips.rocketchip.tilelink._
import freechips.rocketchip.formal._
import freechips.rocketchip.diplomacy._

/** Provides classes for dealing with complex numbers.  Also provides
  *  implicits for converting to and from `Int`.
  *
  *  API to access Diplomacy Node data:
  *  {{{
  *     val baseNodes = SelectDiplomacy.collectBaseNodes()
  *     val impMod = SelectDiplomacy.collectImpModules()
  *     val lazyMod = SelectDiplomacy.collectLazyModules()
  * 
  *     val mixedNodes = SelectDiplomacy.mixedNodes()
  *     val mixedCustomNodes = SelectDiplomacy.mixedCustomNodes()
  *     val customNodes = SelectDiplomacy.customNodes()
  *     val junctionNodes = SelectDiplomacy.junctionNodes()
  *     val mixedAdapterNodes = SelectDiplomacy.mixedAdapterNodes()
  *     val adapterNodes = SelectDiplomacy.adapterNodes()
  *     val identityNodes = SelectDiplomacy.identityNodes()
  *     val ephemeralNodes = SelectDiplomacy.ephemeralNodes()
  *     val mixedNexusNodes = SelectDiplomacy.mixedNexusNodes()
  *     val nexusNodes = SelectDiplomacy.nexusNodes()
  *     val sourceNodes = SelectDiplomacy.sourceNodes()
  *     val sinkNodes = SelectDiplomacy.sinkNodes()
  *     val mixedTestNodes = SelectDiplomacy.mixedTestNodes()
  * 
  *     val csNode = getSrcNode[DCache, TLClientNode]()
  *         ManagementNode
  *         AdapterNode
  *         JumctionNode
  *         NameNode
  *         NexusNode
  *  }}}
 */

case class AopTestModule (wide: Int) extends Module {
  val io = IO(new Bundle {
    val inc = Input(Bool())
    val random = Output(UInt(wide.W))
  })
  io.random := 0.U
}

//class AopTLMonitor(args: TLMonitorArgs, monitorDir: MonitorDirection = MonitorDirection.Monitor) extends TLMonitorBase(args) {
//  val aopMonitor = Module(
//    new FormalTileLinkMonitor(TLMonitorArgs(edge), MonitorDirection.Driver)
//  )
//}

case object TLMonitorInjToDcache extends InjectorAspect[RawModule, DCacheModule](
  {top: RawModule => Select.collectDeep(top) { case d: DCacheModule => d }},
  {d: DCacheModule => 
    // attach TLMonitor here
    printf("SULTAN from object TLMonitorInjToDcache for .v file")
    println(s"DEBUG from object TLMonitorInjToDcache wire inject")
    val dummyWire = Wire(UInt(3.W)).suggestName("aopTestWire")
    dummyWire := 5.U
    dontTouch(dummyWire)

    println(s"DEBUG from object TLMonitorInjToDcache test moule inject")
    val aopTestMod = Module(new AopTestModule(16))
    aopTestMod.io.inc := 1.U
    dontTouch(aopTestMod.io.inc)

//    val args = TLMonitorArgs() // edges comes from select
//
//    val aopTlMod = Module(new TLMonitor(args))

    val clientSrcNode = SelectDiplomacy.getSrcNode[DCache, TLClientNode]()

    println(s"SULTAN Client Source Node params ${clientSrcNode.head.portParams}")

    //    val aopTlMon = Module(new TLMonitor(clients.head.portParams))


    // connect wires
 
    val ios = Select.ios(d)
    println(s"DEBUG DCache ios ${ios}")
    val inst = Select.instances(d)
    println(s"DEBUG DCache inst ${inst}")
    val wires = Select.wires(d)
    println(s"DEBUG DCache wires ${wires}")

    inst.foreach{x => println(s"SULTAN DCACHE INST ${x}")}
    val bun = wires.collect{case x: TLBundleA => println(s"SULTAN TLBundleA x: ${x}"); x}

//    val aaaa = TLImp.monitor(bun.head, TLEdgeIn())

    val view = SelectDiplomacy().viewDiplomacy()

  })


