package doodle
package chart

package object syntax extends ToPointSyntax
    with ToSeriesSyntax
    with ChartSyntax
{
  object toPoint extends ToPointSyntax
  object toSeries extends ToSeriesSyntax
  object chart extends ChartSyntax
}
